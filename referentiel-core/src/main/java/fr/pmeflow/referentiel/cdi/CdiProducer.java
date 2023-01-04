/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.referentiel.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.authentification.BearerAuthenticator;
import fr.pmeflow.stripe.ProductService;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.client.RegistryServiceClient;
import fr.lixbox.service.registry.model.ServiceEntry;

/**
 * Cette classe assure la production des clients pour le CDI
 * 
 * @author Ludovic.terral
 */
public class CdiProducer
{
    // ----------- Attribut(s) -----------  
    private static final Log LOG = LogFactory.getLog(CdiProducer.class);
    
    private static final String DEFAULT_REGISTRY_URL = "http://localhost:18100/registry/api/1.0";
    @ConfigProperty(name="registry.uri", defaultValue=DEFAULT_REGISTRY_URL) String registryUri;
    @ConfigProperty(name="redis.uri", defaultValue=DEFAULT_REGISTRY_URL) String redisUri;
    @ConfigProperty(name="stripe.service.uri") String stripeServiceUri;

    @Inject JsonWebToken jwt; 
    
    

    // ----------- Methode(s) -----------
    @Produces @LocalRegistryConfig
    public RegistryServiceClient getRegistryServiceClient() 
    {
        RegistryServiceClient result = null;
        try
        {
            result = new RegistryServiceClient(registryUri);
        }
        catch(Exception e)
        {
            //absence de service d'annuaire.
        }
        return result;
    }
        
    
    
    @Produces @LocalRegistryConfig
    public ExtendRedisClient getExtendRedisClient() 
    {
        ExtendRedisClient result = null;
        try
        {
            if (StringUtil.isEmpty(redisUri))
            {
                ServiceEntry redis = getRegistryServiceClient().discoverService(PmeflowConstant.REDIS_NAME, PmeflowConstant.REDIS_VERSION);
                if (redis!=null)
                {
                    redisUri = redis.getPrimary().getUri();
                }
            }
            if (StringUtil.isNotEmpty(redisUri))
            {
                result = new ExtendRedisClient(redisUri);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public ProductService getProductService() 
    {
        ProductService result = null;
        try
        {
            if (StringUtil.isNotEmpty(stripeServiceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(stripeServiceUri));
                result = target.proxy(ProductService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
}
