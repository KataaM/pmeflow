/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.referentiel.parametre.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.referentiel.abonnement.RefAbonnementService;
import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.pmeflow.referentiel.parametre.model.Parametre;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.model.EQuery;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.RegistryService;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;

/**
 * Ce service de parametres fonctionne sur Redis.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Path(ParametreService.SERVICE_URI)
@Produces({"application/json"})
@Consumes({"application/json"})
public class ParametreServiceBean implements ParametreService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 201705120951L;
    private static final Log LOG = LogFactory.getLog(ParametreService.class);

    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";


    @Inject @LocalRegistryConfig RegistryService registryService;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;



    // ----------- Methode -----------
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
    public Parametre getParametreById(String id) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "id" }));   
        }
        
        Parametre paramGps=null;
        try
        {
            paramGps = redisClient.findById(Parametre.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return paramGps;
    }



    @Override
    public Parametre synchronize(Parametre param) throws BusinessException
    {
        try
        {
            ConteneurEvenement events = param.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {ParametreService.SERVICE_CODE}, Langue.FR_FR), events);
            }
            param = redisClient.merge(param);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return param;
    }



    @Override
    public boolean remove(String oid) throws BusinessException
    {
        boolean result = false;
        try
        {
            redisClient.remove(Parametre.class, oid);
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return result;
    }



    @Override
    public List<Parametre> getParametres() throws BusinessException
    {
        List<Parametre> params=new ArrayList<>();
        try
        {   
            params.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+Parametre.class.getName()+"*")));
            params = CollectionUtil.sortCollectionAsc("code", params);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RefAbonnementService.SERVICE_CODE, false);
        }
        return params;
    }



    @Override
    public List<Parametre> getParametresByService(String serviceId) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "serviceId" }));   
        }
        List<Parametre> params=new ArrayList<>();
        try
        {
            EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("service",serviceId));
            query.limit(0, 500);
            query.setSortBy("code", true);
            params.addAll(redisClient.findByExpression(Parametre.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return params;
    }



    @Override
    public Parametre getParametreByCode(String serviceId, String code) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "serviceId" }));   
        }
        if (StringUtil.isEmpty(code))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "code" }));   
        }
        
        Parametre parametre=null;
        try
        {
            List<Parametre> parametres = new ArrayList<>();
            EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("code",code)+" "+
                    RedisSearchQueryHelper.toTextField("service",serviceId));
            query.limit(0, 500);
            query.setSortBy("code", true);
            parametres.addAll(redisClient.findByExpression(Parametre.class, query));
            if (parametres.size()==1)
            {
                parametre=parametres.get(0);
            }
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return parametre;
    }



    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValueByCode(String serviceId, String code, String defaultValue, String defaultValueClass)
            throws BusinessException
    {
        T value=null;
        try
        {
            Parametre param = getParametreByCode(serviceId, code);
            if (param!=null && "java.lang.String".equals(param.getValueClass()))
            {
                value=(T) param.getValue();
            }
            else if (param!=null && StringUtil.isNotEmpty(param.getValue()) && StringUtil.isNotEmpty(param.getValueClass()))
            {
                value = extractTypedValue(param.getValueClass(), param.getValue());
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, !(StringUtil.isNotEmpty(defaultValue)&&StringUtil.isNotEmpty(defaultValueClass)));
        }
        if (value==null)
        {
            value = extractTypedValue(defaultValueClass, defaultValue);
        }
        return value;
    }

    
    
    @SuppressWarnings("unchecked")
    private <T> T extractTypedValue(String classz, String json) throws BusinessException
    {
        T value=null;
        try
        {
            if (classz.contains("String"))
            {
                value=(T) json;
            }
            else
            {
                final Class<?> typed = Class.forName(classz);
                TypeReference<?> typeRef= new TypeReference<Object>()
                {
                    @Override
                    public Type getType() {
                        return typed;
                    }
                };
                value = (T) JsonUtil.transformJsonToObject(json, typeRef);
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return value;
    }
}