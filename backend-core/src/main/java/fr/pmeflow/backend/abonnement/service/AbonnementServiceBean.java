/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.abonnement.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.pmeflow.backend.abonnement.AbonnementService;
import fr.pmeflow.backend.abonnement.model.Abonnement;
import fr.pmeflow.backend.abonnement.model.StatutAbonnement;
import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.backend.prestation.model.StatutPrestation;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.pmeflow.referentiel.abonnement.RefAbonnementService;
import fr.pmeflow.referentiel.abonnement.model.RefAbonnement;
import fr.pmeflow.stripe.PaymentService;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.DateUtil;
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
 * Ce service implémente le référentiel des abonnements.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"READ_ABONNEMENT", "WRITE_ABONNEMENT"})
public class AbonnementServiceBean implements AbonnementService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202111162338L;
    private static final Log LOG = LogFactory.getLog(AbonnementService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String DD_MM_YYYY_FORMAT = "dd/MM/yyyy";
    private static final String DD_MM_YYYY_HH_MM_SS_FORMAT = "dd/MM/yyyy HH:mm:ss";

    @Inject @LocalRegistryConfig PaymentService paymentService;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    @Inject @Named("prestationService-user-connected") PrestationService prestationService;
    @Inject @LocalRegistryConfig RefAbonnementService refAbonnementService;
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
    @RolesAllowed("READ_REF_ABONNEMENT")
    public Abonnement prepareAbonnement(Langue langue, String id) throws BusinessException
    {
        Abonnement result = null;
        try
        {
            RefAbonnement ref = refAbonnementService.getAbonnementById(langue, id);
            result = new Abonnement(ref);
            result.setEtat(StatutAbonnement.A_VERIFIER);
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, true);
        }
        return result;
    }
    


    @Override
    public List<Abonnement> getAbonnements(Langue langue) throws BusinessException
    {
        List<Abonnement> abonnements = new ArrayList<>();
        try
        {   
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("client_id", jwt.getSubject()));
                abonnements.addAll(redisClient.findByExpression(Abonnement.class, query));
            }
            else
            {
                abonnements.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+Abonnement.class.getName()+"*")));
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, false);
        }
        return abonnements;
    }



    @Override
    @RolesAllowed({"READ_ABONNEMENT"})
    public Abonnement getAbonnementById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { AbonnementService.SERVICE_CODE, "id" },langue));   
        }
        
        Abonnement abonnement=null;
        try
        {
            abonnement = redisClient.findById(Abonnement.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, true);
        }
        return abonnement;
    }

    

    @Override
    @RolesAllowed({"READ_ABONNEMENT"})
    public List<Abonnement> getAbonnementsByExpression(Langue langue, String query) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(query))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { AbonnementService.SERVICE_CODE, "query" },langue));   
        }
        
        List<Abonnement> abonnement=new ArrayList<>();
        try
        {
            abonnement.addAll(redisClient.findByExpression(Abonnement.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, true);
        }
        return abonnement;
    }
    
    

    @Override
    @RolesAllowed({"WRITE_ABONNEMENT"})
    public Abonnement mergeAbonnement(Langue langue, Abonnement abonnement) throws BusinessException
    {
        try
        {
            ConteneurEvenement events = abonnement.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {AbonnementService.SERVICE_CODE}, langue), events);
            }         
            switch (abonnement.getEtat())
            {
                case A_VERIFIER:
                    abonnement.setVerifPaymentId(paymentService.preparePayment(langue, 100, 
                            "Vérification du moyen de paiement de l'" + abonnement.toPrettyString(), 
                            abonnement.getClient().getCustomerStripeId(), true));
                    break;
                case ACTIF:
                    if (!existPrestationForDate(langue, abonnement.getDateDebut(), abonnement.getOid()))
                    {
                        Prestation prestation = new Prestation();
                        prestation.setClient(abonnement.getClient());
                        prestation.setDateAchat(abonnement.getDateDebut());
                        prestation.setLibelle("Vérification du moyen de paiement fourni pour l'abonnement");
                        prestation.setDescription("Nous vérifions le moyen de paiement en effectuant un paiement H.T. de 1€ qui sera déduit lors du premier prélèvement.");
                        prestation.setAbonnementId(abonnement.getOid());
                        prestation.setEtat(StatutPrestation.PAYE);
                        prestation.setPaymentIntentId(abonnement.getVerifPaymentId());
                        prestation.setPrixHT(1.0);
                        prestationService.mergePrestation(langue, prestation);
                    }
                    break;
                default:
                    break;
            }
            abonnement = redisClient.merge(abonnement);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, true);
        }
        return abonnement;
    }
    
    

    @Override
    @RolesAllowed({"WRITE_ABONNEMENT"})
    public Abonnement resilierAbonnement(Langue langue, String abonnementId, String motif) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(abonnementId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { AbonnementService.SERVICE_CODE, "abonnementId" },langue));   
        }
        if (StringUtil.isEmpty(motif))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { AbonnementService.SERVICE_CODE, "motif" },langue));   
        }
        
        Abonnement abonnement = null;
        try
        {
            abonnement = getAbonnementById(langue, abonnementId);
            
            //si fin periode engagement
            Date finEngagement = abonnement.getDateDebut();
            finEngagement = DateUtil.addMonths(finEngagement, abonnement.getDureeEngagement());
            
            Date retractation = abonnement.getDateDebut();
            retractation = DateUtil.addDays(retractation, 14);
            
            if (new Date().after(finEngagement) || new Date().before(retractation))
            {
                abonnement.setEtat(StatutAbonnement.RESILIE);
                abonnement.setMotifResiliation(motif);
            }
            else
            {
                abonnement.setEtat(StatutAbonnement.EN_COURS_DE_RESILIATION);
            }
            abonnement = redisClient.merge(abonnement);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, true);
        }
        return abonnement;
    }



    @Override
    @RolesAllowed({"WRITE_PRESTATION"})
    public boolean facturerMensualite(Langue langue, int refDay) throws BusinessException
    {
        //Controler les parametres
        if (refDay<1 && refDay>31)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { AbonnementService.SERVICE_CODE, "query" },langue));   
        }
        
        Calendar refDate = Calendar.getInstance();
        refDate.set(Calendar.DAY_OF_MONTH, refDay);        
        Date dateDebutMin =  DateUtil.parseDate(DateUtil.format(DateUtil.addDays(new Date(), -28), DD_MM_YYYY_FORMAT)+" 00:00:00", DD_MM_YYYY_HH_MM_SS_FORMAT);
                
        List<Abonnement> abonnements=new ArrayList<>();
        try
        {
            StringBuilder query = new StringBuilder(RedisSearchQueryHelper.toLowerEqualThanField("dateDebut", dateDebutMin.getTime()));
            query.append(" ");
            query.append(RedisSearchQueryHelper.toNumericField("jourPrelevement", refDay+""));
            query.append(" ");
            query.append(RedisSearchQueryHelper.toNotCriteria(RedisSearchQueryHelper.toTextField("etat", StatutAbonnement.RESILIE.name())));
            abonnements.addAll(redisClient.findByExpression(Abonnement.class, query.toString()));
            
            for (Abonnement abonnement : abonnements) 
            {
                if (!existPrestationForDate(langue, refDate.getTime(), abonnement.getOid()))
                {
                    Prestation prestation = preparePrestationForMensualite(langue, DateUtil.format(refDate, "MM/yyyy"), abonnement);
                    if (StatutPrestation.A_PAYER.equals(prestation.getEtat()))
                    {
                        abonnement.setEtat(StatutAbonnement.EN_ERREUR);
                    }
                    else
                    {
                        abonnement.setEtat(StatutAbonnement.ACTIF);
                    }
                    prestationService.mergePrestation(langue, prestation);
                    mergeAbonnement(langue, abonnement);
                }
            }
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, false);
        }
        return true;
    }



    private boolean existPrestationForDate(Langue langue, Date refDate, String abonnementId) throws BusinessException
    {
        boolean result = false;
        try
        {
            Date refDateMin =  DateUtil.parseDate(DateUtil.format(refDate, DD_MM_YYYY_FORMAT)+" 00:00:00", DD_MM_YYYY_HH_MM_SS_FORMAT);
            Date refDateMax =  DateUtil.parseDate(DateUtil.format(refDate, DD_MM_YYYY_FORMAT)+" 23:59:59", DD_MM_YYYY_HH_MM_SS_FORMAT);
            
            StringBuilder query = new StringBuilder(RedisSearchQueryHelper.toGreaterEqualThanField("dateAchat", refDateMin.getTime()));
            query.append(" ");
            query.append(RedisSearchQueryHelper.toLowerEqualThanField("dateAchat", refDateMax.getTime()));
            query.append(" ");
            query.append(RedisSearchQueryHelper.toTextField("abonnement_id", abonnementId));
            LOG.debug(prestationService.getPrestationsByExpression(Langue.FR_FR, query.toString()));
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, AbonnementService.SERVICE_CODE, false);
        }                
        return result;
    }



    private Prestation preparePrestationForMensualite(Langue langue, String mensualite, Abonnement abonnement)
            throws BusinessException
    {
        Prestation prestation = new Prestation();
        prestation.setClient(abonnement.getClient());
        prestation.setDateAchat(new Date());
        prestation.setLibelle("Mensualité "+mensualite+" - "+abonnement.getLibelle());
        prestation.setDescription(abonnement.getDescription());
        prestation.setAbonnementId(abonnement.getOid());
        prestation.setPaymentIntentId(paymentService.payMonthlyInstallment(langue, abonnement.getPrixHT(), 
                prestation.toPrettyString(), abonnement.getClient().getCustomerStripeId(), abonnement.getPaymentMethodId()));
        if (prestationService.getPrestationsByAbonnement(langue, abonnement.getOid()).size()<2)
        {
            prestation.setPrixHT((abonnement.getPrixHT()-1));
        }
        if (StringUtil.isNotEmpty(prestation.getPaymentIntentId())) 
        {
            prestation.setEtat(StatutPrestation.PAYE);
        }
        else
        {
            prestation.setEtat(StatutPrestation.A_PAYER);
        }
        return prestation;
    }
}