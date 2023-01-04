/*******************************************************************************
 *    
 *                           APPLICATION PMEFLOW
 *                          =====================
 *                          
 *   Copyright (C) 2002 Lixtec-Ludovic TERRAL
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   @AUTHOR Lixtec-Ludovic TERRAL
 *
 ******************************************************************************/
package fr.pmeflow.iam.cdi;

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
import org.keycloak.admin.client.resource.RealmResource;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.authentification.BearerAuthenticator;
import fr.pmeflow.iam.helper.ExtendKeycloak;
import fr.pmeflow.stripe.CustomerService;
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
    @ConfigProperty(name="stripe.service.uri") String stripeServiceUri;
    
    @ConfigProperty(name="keycloak-url") String keycloakUri;
    @ConfigProperty(name="keycloak-realm") String keycloakRealm;
    @ConfigProperty(name="keycloak-username") String keycloakUsername;
    @ConfigProperty(name="keycloak-pwd") String keycloakPassword;
    @ConfigProperty(name="keycloak-client-id") String keycloakClientId;
    
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
    public CustomerService getCustomerService() 
    {
        CustomerService result = null;
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
                result = target.proxy(CustomerService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
        
    @Produces @LocalRegistryConfig
    public RealmResource getKeycloakRealm() 
    {
        RealmResource result = null;
        try
        {
            result = ExtendKeycloak.getInstance(keycloakUri, keycloakRealm, keycloakUsername, keycloakPassword, keycloakClientId).realm(keycloakRealm);
        }
        catch(Exception e)
        {
            //absence de service keycloak.
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public ExtendRedisClient getExtendRedisClient() 
    {
        ExtendRedisClient result = null;
        try
        {
            String redisUri = System.getProperty("redis.uri");
            if (StringUtil.isNotEmpty(redisUri))
            {
                ServiceEntry redis = getRegistryServiceClient().discoverService(PmeflowConstant.REDIS_NAME, PmeflowConstant.REDIS_VERSION);
                if (redis!=null)
                {
                    redisUri = redis.getPrimary().getUri();
                }
            }
            if (!StringUtil.isEmpty(redisUri))
            {
                String hostName = redisUri.substring(6,redisUri.lastIndexOf(':'));
                String port = redisUri.substring(redisUri.lastIndexOf(':')+1);
                result = new ExtendRedisClient(hostName, Integer.parseInt(port));
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
}
