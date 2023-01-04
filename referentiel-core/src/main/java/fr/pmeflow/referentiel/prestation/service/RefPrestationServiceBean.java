/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.referentiel.prestation.service;

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
import fr.pmeflow.referentiel.prestation.RefPrestationService;
import fr.pmeflow.referentiel.prestation.model.RefPrestation;
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
 * Ce service implémente le référentiel des prestations.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"READ_REF_PRESTATION", "WRITE_REF_PRESTATION"})
public class RefPrestationServiceBean implements RefPrestationService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202111162338L;
    private static final Log LOG = LogFactory.getLog(RefPrestationService.class);
    
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
    @RolesAllowed("READ_REF_PRESTATION")
    public List<RefPrestation> getPrestations(Langue langue) throws BusinessException
    {
        List<RefPrestation> prestations = new ArrayList<>();
        try
        {   
            prestations.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+RefPrestation.class.getName()+"*")));
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RefPrestationService.SERVICE_CODE, false);
        }
        return prestations;
    }



    @Override
    @RolesAllowed("READ_REF_PRESTATION")
    public RefPrestation getPrestationById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { RefPrestationService.SERVICE_CODE, "id" },langue));   
        }
        
        RefPrestation prestation=null;
        try
        {
            prestation = redisClient.findById(RefPrestation.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, RefPrestationService.SERVICE_CODE, true);
        }
        return prestation;
    }
    
    

    @Override
    @RolesAllowed("WRITE_REF_PRESTATION")
    public RefPrestation mergePrestation(Langue langue, RefPrestation prestation) throws BusinessException
    {
        //merge product on stripe
        try
        {
            prestation=productService.mergeRefPrestation(langue, prestation);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, RefPrestationService.SERVICE_CODE, false);
        }
        
        try
        {
            ConteneurEvenement events = prestation.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {RefPrestationService.SERVICE_CODE}, langue), events);
            }
            prestation = redisClient.merge(prestation);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, RefPrestationService.SERVICE_CODE, true);
        }
        return prestation;
    }
}