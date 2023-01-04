/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.prestation.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.rabbitmq.client.Channel;

import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.backend.prestation.model.PrestationEventBusMetadata;
import fr.pmeflow.backend.prestation.model.StatutPrestation;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.pmeflow.referentiel.prestation.RefPrestationService;
import fr.pmeflow.referentiel.prestation.model.RefPrestation;
import fr.pmeflow.stripe.PaymentService;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
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
 * Ce service implémente le référentiel des prestations.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"READ_PRESTATION", "WRITE_PRESTATION"})
public class PrestationServiceBean implements PrestationService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202120402201L;
    private static final Log LOG = LogFactory.getLog(PrestationService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";

    
    @Inject @LocalRegistryConfig Channel channel;
    
    @Inject @LocalRegistryConfig PaymentService paymentService;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;    
    @Inject @LocalRegistryConfig RefPrestationService refPrestationService;
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
    @RolesAllowed({"READ_REF_PRESTATION"})
    public Prestation preparePrestation(Langue langue, String id) throws BusinessException
    {
        Prestation result = null;
        try
        {
            RefPrestation ref = refPrestationService.getPrestationById(langue, id);
            result = new Prestation(ref);
        }        
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, true);
        }
        return result;
    }
    


    @Override
    public List<Prestation> getPrestations(Langue langue) throws BusinessException
    {
        List<Prestation> prestations = new ArrayList<>();
        try
        {   
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("client_id", jwt.getSubject()));
                prestations.addAll(redisClient.findByExpression(Prestation.class, query));
            }
            else
            {
                prestations.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+Prestation.class.getName()+"*")));
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, false);
        }
        return prestations;
    }



    @Override
    @RolesAllowed({"READ_PRESTATION"})
    public Prestation getPrestationById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "id" },langue));   
        }
        
        Prestation prestation=null;
        try
        {
            prestation = redisClient.findById(Prestation.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, true);
        }
        return prestation;
    }

    

    @Override
    @RolesAllowed({"READ_PRESTATION"})
    public List<Prestation> getPrestationsByAbonnement(Langue langue, String abonnementId) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(abonnementId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "abonnementId" }, langue));   
        }
        
        List<Prestation> prestation=new ArrayList<>();
        try
        {
            String query  = RedisSearchQueryHelper.toTextField("abonnement_id", abonnementId);
            prestation.addAll(redisClient.findByExpression(Prestation.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, false);
        }
        return prestation;
    }

    

    @Override
    @RolesAllowed({"READ_PRESTATION"})
    public List<Prestation> getPrestationsByDossier(Langue langue, String dossierId) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(dossierId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "dossierId" }, langue));   
        }
        
        List<Prestation> prestation=new ArrayList<>();
        try
        {
            String query  = RedisSearchQueryHelper.toTextField("dossier_id", dossierId);
            prestation.addAll(redisClient.findByExpression(Prestation.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, false);
        }
        return prestation;
    }

    

    @Override
    @RolesAllowed({"READ_PRESTATION"})
    public List<Prestation> getPrestationsByExpression(Langue langue, String query) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(query))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "query" },langue));   
        }
        
        List<Prestation> prestation=new ArrayList<>();
        try
        {
            prestation.addAll(redisClient.findByExpression(Prestation.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, true);
        }
        return prestation;
    }
    
    

    @Override
    @RolesAllowed({"WRITE_PRESTATION"})
    public Prestation mergePrestation(Langue langue, Prestation prestation) throws BusinessException
    {
        try
        {
            if (prestation.getDateAchat()==null)
            {
                prestation.setDateAchat(Date.from(Instant.now()));
            }

            ConteneurEvenement events = prestation.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {PrestationService.SERVICE_CODE}, langue), events);
            }
            if (StringUtil.isEmpty(prestation.getOid()))
            {
                prestation = redisClient.merge(prestation);
            }
            switch (prestation.getEtat())
            {
                case A_PAYER:
                    prestation.setPaymentIntentId(paymentService.preparePayment(langue, prestation.getPrixHT(), prestation.toPrettyString(), prestation.getClient().getCustomerStripeId(), false));
                    channel.basicPublish(PrestationEventBusMetadata.EXCHANGE, PrestationEventBusMetadata.EVENT_PRESTATION_TO_PAY, null, prestation.toString().getBytes(StandardCharsets.UTF_8));       
                    break;
                case PAYE:
                    channel.basicPublish(PrestationEventBusMetadata.EXCHANGE, PrestationEventBusMetadata.EVENT_PRESTATION_PAYED, null, prestation.toString().getBytes(StandardCharsets.UTF_8));       
                    break;
                default:
                    break;
            }
            prestation = redisClient.merge(prestation);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, true);
        }
        return prestation;
    }
    
    

    @Override
    @RolesAllowed({"WRITE_PRESTATION"})
    public boolean removePrestation(Langue langue, String prestationId) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(prestationId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "prestationId" },langue));   
        }
        
        boolean result = false;
        try
        {
            Prestation prestation = getPrestationById(langue, prestationId);
            if (StatutPrestation.A_PAYER.equals(prestation.getEtat()))
            {
                redisClient.remove(Prestation.class, prestationId);
                result = true;
            }
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PrestationService.SERVICE_CODE, true);
        }
        return result;
    }
}