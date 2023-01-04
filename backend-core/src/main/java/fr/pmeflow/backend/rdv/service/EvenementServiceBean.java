/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.rdv.service;

import java.io.Serializable;
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
import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.rdv.EvenementService;
import fr.pmeflow.backend.rdv.model.Evenement;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.model.EQuery;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;

/**
 * Cette classe expose les fonctions de gestion des evenement
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({ "READ_EVENT", "WRITE_EVENT" })
public class EvenementServiceBean implements Serializable, EvenementService
{
	// ----------- Attribut -----------
	private static final long serialVersionUID = 202205101129L;
    private static final Log LOG = LogFactory.getLog(EvenementService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";

    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
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
    public List<Evenement> getEvenements(Langue langue) throws BusinessException
    {
        List<Evenement> events = new ArrayList<>();
        try
        {   
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            if (connected.getProfils().contains(ProfilPmeflow.ADMIN))
            {
                events.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+Evenement.class.getName()+"*")));   
            }
            else
            {
                EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("utilisateur_id", jwt.getSubject()));
                events.addAll(redisClient.findByExpression(Evenement.class, query));
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, EvenementService.SERVICE_CODE, false);
        }
        return events;
    }



    @Override
    @RolesAllowed({ "WRITE_EVENT" })
    public boolean removeEvenement(Langue langue, String id) throws BusinessException
    {
        boolean result = false;
        try
        {
            redisClient.remove(Evenement.class, id);
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, EvenementService.SERVICE_CODE, true);
        }
        return result;
    }



    @Override
    @RolesAllowed({ "WRITE_EVENT" })
    public Evenement mergeEvenement(Langue langue, Evenement event) throws BusinessException
    {
        try
        {
            ConteneurEvenement events = event.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03",
                        new String[] { EvenementService.SERVICE_CODE }, langue), events);
            }
            event = redisClient.merge(event);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, EvenementService.SERVICE_CODE, true);
        }
        return event;
    }
    
    

    @Override
    public Evenement getEvenementById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "id" },langue));   
        }
        
        Evenement result=null;
        try
        {
            result = redisClient.findById(Evenement.class, id);
        }
        catch(BusinessException e)
        {
            LOG.error(e);
            throw e;
        }
        catch(Exception e)
        {
            LOG.error(e);
        }
        return result;
    }
}