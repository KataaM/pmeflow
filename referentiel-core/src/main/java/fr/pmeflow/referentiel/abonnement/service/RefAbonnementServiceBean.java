/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.referentiel.abonnement.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.referentiel.abonnement.RefAbonnementService;
import fr.pmeflow.referentiel.abonnement.model.RefAbonnement;
import fr.pmeflow.referentiel.prestation.RefPrestationService;
import fr.pmeflow.stripe.ProductService;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
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
@RolesAllowed({"READ_REF_ABONNEMENT", "WRITE_REF_ABONNEMENT"})
public class RefAbonnementServiceBean implements RefAbonnementService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202111162338L;
    private static final Log LOG = LogFactory.getLog(RefAbonnementService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";

    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    @Inject @LocalRegistryConfig ProductService productService;    
    
    
    
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
    public List<RefAbonnement> getAbonnements(Langue langue) throws BusinessException
    {
        List<RefAbonnement> abonnements = new ArrayList<>();
        try
        {   
            abonnements.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+RefAbonnement.class.getName()+"*")));
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RefAbonnementService.SERVICE_CODE, false);
        }
        return abonnements;
    }



    @Override
    @RolesAllowed("READ_REF_ABONNEMENT")
    public RefAbonnement getAbonnementById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { RefAbonnementService.SERVICE_CODE, "id" }, langue));   
        }
        
        RefAbonnement abonnement=null;
        try
        {
            abonnement = redisClient.findById(RefAbonnement.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, RefAbonnementService.SERVICE_CODE, true);
        }
        return abonnement;
    }
    
    

    @Override
    @RolesAllowed("WRITE_REF_ABONNEMENT")
    public RefAbonnement mergeAbonnement(Langue langue, RefAbonnement abonnement) throws BusinessException
    {
        //merge product on stripe
        try
        {
            abonnement = productService.mergeRefAbonnement(langue, abonnement);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, RefPrestationService.SERVICE_CODE, false);
        }
        
        try
        {
            ConteneurEvenement events = abonnement.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {RefAbonnementService.SERVICE_CODE}, langue), events);
            }
            abonnement = redisClient.merge(abonnement);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, RefAbonnementService.SERVICE_CODE, true);
        }
        return abonnement;
    }
}