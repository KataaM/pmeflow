/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.dossier.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;

import fr.pmeflow.backend.abonnement.AbonnementService;
import fr.pmeflow.backend.abonnement.model.Abonnement;
import fr.pmeflow.backend.abonnement.model.StatutAbonnement;
import fr.pmeflow.backend.dossier.DossierService;
import fr.pmeflow.backend.dossier.model.Dossier;
import fr.pmeflow.backend.dossier.model.DossierEventBusMetadata;
import fr.pmeflow.backend.dossier.model.enumeration.EtatDossier;
import fr.pmeflow.backend.io.EmailService;
import fr.pmeflow.backend.io.model.Document;
import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.EventBusMetadata;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.model.EQuery;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;

/**
 * Ce service implémente le référentiel des dossiers.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"READ_DOSSIER", "WRITE_DOSSIER"})
public class DossierServiceBean implements DossierService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202111162338L;
    private static final Log LOG = LogFactory.getLog(DossierService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";

    @Inject @LocalRegistryConfig Channel channel;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    @Inject @Named("abonnementService-user-connected") AbonnementService abonnementService;
    @Inject @Named("prestationService-user-connected") PrestationService prestationService;
    @Inject @LocalRegistryConfig EmailService emailService;
    @Inject @LocalRegistryConfig UtilisateurService utilisateurService;
    
    @Inject JsonWebToken jwt;
    
    
    
    // ----------- Methode(s) -----------
    @Override
    @PermitAll
    public ServiceState checkHealth() 
    {
        return checkReady();
    }

    
    
    @Override
    @PermitAll
    public ServiceState checkReady()
    {
        LOG.debug("Check Health started");
        ServiceState state = new ServiceState();
        state.setStatus(ServiceStatus.UP);
        try
        {
            redisClient.ping();
            state.getChecks().add(new Check(ServiceStatus.UP, PmeflowResources.getString("MSG.SERVICE.HEALTH", new Object[] {"redisClient"}, Langue.FR_FR)));   
        }
        catch(Exception e)
        {
            state.getChecks().add(new Check(ServiceStatus.DOWN, PmeflowResources.getString("MSG.SERVICE.NOT.HEALTH", new Object[]{"redisClient"}, Langue.FR_FR)));            
        }
        if (state.getStatus().equals(ServiceStatus.DOWN))
        {
            throw new ProcessusException(state.toString());
        }
        return state;
    }
    
    
    
    @Override
    @PermitAll
    public ServiceState checkLive() 
    {
        return new ServiceState(ServiceStatus.UP);
    }



    /**
     * Cette methode renvoie la version courante du code. 
     */
    @Override
    @PermitAll
    public String getVersion()
    {   
        return CodeVersionUtil.getVersion(this.getClass());
    }


    
    @Override
    @RolesAllowed("READ_DOSSIER")
    public boolean verifDossierCreate(@PathParam("langue") Langue langue) throws BusinessException
    {
        boolean result = false;
        try
        {
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                // possede un abonnemnt avec credit
                List<Abonnement> abonnements = abonnementService.getAbonnements(langue);
                int credit = 0;
                for (Abonnement abo : abonnements)
                {
                    if (!(abo.getEtat()==StatutAbonnement.A_VERIFIER || 
                            abo.getEtat()==StatutAbonnement.RESILIE ||
                            abo.getEtat()==StatutAbonnement.EN_ERREUR))
                    {
                        credit += abo.getCreditsDossier();
                    }
                }
                List<Dossier> dossiers = getDossiers(langue);
                for (Dossier dossier : dossiers)
                {
                    if (dossier.getEtat()!=EtatDossier.CLOS)
                    {
                        credit-=1;
                    }
                }
                if (credit>0)
                {
                    result=true;
                }
                
                // possede une presta avec credit
                List<Prestation> prestations = prestationService.getPrestations(langue);
                credit = 0;
                for (Prestation presta : prestations)
                {
                    credit += presta.getCreditsDossier();
                }
                for (Dossier dossier : dossiers)
                {
                    if (dossier.getEtat()!=EtatDossier.CLOS)
                    {
                        credit-=1;
                    }
                }
                if (credit>0)
                {
                    result=true;
                }             
            }
            else
            {
                result = true;   
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, false);
        }
        return result;
    }
    


    @Override
    @RolesAllowed("READ_DOSSIER")
    public List<Dossier> getDossiers(Langue langue) throws BusinessException
    {
        List<Dossier> dossiers = new ArrayList<>();
        try
        {
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("clientId", jwt.getSubject()));
                dossiers.addAll(redisClient.findByExpression(Dossier.class, query));                
            }
            else
            {
                dossiers.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+Dossier.class.getName()+"*")));   
            } 
            dossiers = CollectionUtil.sortCollectionAsc("libelle", dossiers);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, false);
        }
        return dossiers;
    }
    


    @Override
    @RolesAllowed("READ_DOSSIER")
    public List<Dossier> getDossiersActifs(Langue langue) throws BusinessException
    {
        List<Dossier> dossiers = new ArrayList<>();
        try
        {
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            StringBuilder query = new StringBuilder();
            if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                query.append(RedisSearchQueryHelper.toTextField("clientId", jwt.getSubject())+" ");
            }
            query.append(RedisSearchQueryHelper.toNotCriteria(RedisSearchQueryHelper.toTextField("etat", EtatDossier.CLOS.name())));   
            dossiers.addAll(redisClient.findByExpression(Dossier.class, query.toString()));       
            dossiers = CollectionUtil.sortCollectionAsc("libelle", dossiers);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, false);
        }
        return dossiers;
    }


    @Override
    @RolesAllowed("READ_DOSSIER")
    public Dossier getDossierById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { DossierService.SERVICE_CODE, "id" },langue));   
        }
        
        Dossier dossier=null;
        try
        {
            dossier = redisClient.findById(Dossier.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, true);
        }
        return dossier;
    }
    
    

    @Override
    @RolesAllowed("WRITE_DOSSIER")
    public Dossier mergeDossier(Langue langue, Dossier dossier) throws BusinessException
    {
        try
        {
            if (dossier.getDateCreation()==null)
            {
                dossier.setDateCreation(Calendar.getInstance());
            }
            ConteneurEvenement events = dossier.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {DossierService.SERVICE_CODE}, langue), events);
            }
            boolean isNew = StringUtil.isEmpty(dossier.getOid());
            dossier = redisClient.merge(dossier);
            

            
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            Map<String, Object> headers = new HashMap<>();
            if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                if (dossier.getJuriste()==null)
                {
                    headers.put(EventBusMetadata.INFORM_MAIL_ADDRESS, getAllJuristesMail(langue));
                    headers.put(EventBusMetadata.MAJ_BY_CLIENT, true);
                }
                else 
                {
                    headers.put(EventBusMetadata.INFORM_MAIL_ADDRESS, dossier.getJuriste().getEmail());
                    headers.put(EventBusMetadata.MAJ_BY_CLIENT, true);
                }
            }
            else
            {
                if (dossier.getClient()!=null)
                {
                    headers.put(EventBusMetadata.INFORM_MAIL_ADDRESS, dossier.getClient().getEmail());
                    headers.put(EventBusMetadata.MAJ_BY_CLIENT, false);
                }
            }
            
            Builder propBuilder = new BasicProperties.Builder();
            propBuilder.headers(headers);
            if (isNew)
            {
                channel.basicPublish(DossierEventBusMetadata.EXCHANGE, DossierEventBusMetadata.EVENT_DOSSIER_CREATED, propBuilder.build(), dossier.toString().getBytes(StandardCharsets.UTF_8));
            }
            else
            {
                channel.basicPublish(DossierEventBusMetadata.EXCHANGE, DossierEventBusMetadata.EVENT_DOSSIER_UPDATED, propBuilder.build(), dossier.toString().getBytes(StandardCharsets.UTF_8));   
            }
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, true);
        }
        return dossier;
    }
    
    
    
    public boolean addDocumentToDossier(Langue langue, String id, Document document) throws BusinessException
    {
        boolean result = false;
        try
        {
            Dossier dossier = getDossierById(langue, id);
            dossier.getDocuments().add(document);
            mergeDossier(langue, dossier);
            result = true;
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, true);
        }
        return result;
    }
    
    
    private String getAllJuristesMail(Langue langue) 
    {
        List<String> emails = new ArrayList<>();
        try
        {
            List<Utilisateur> utilisateurs = utilisateurService.getJuristes(langue);
            emails.addAll(utilisateurs.stream().map(Utilisateur::getEmail).collect(Collectors.toList()));            
        }
        catch (Exception e)
        {
            LOG.error(e);
        }
        return StringUtil.join(emails, ";");
    }
}