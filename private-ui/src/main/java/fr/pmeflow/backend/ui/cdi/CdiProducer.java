/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.ui.cdi;

import javax.enterprise.inject.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;

/**
 * Cette classe assure la production des clients pour le CDI
 * 
 * @author Ludovic.terral
 */
public class CdiProducer
{
    // ----------- Attribut(s) -----------  
    private static final Log LOG = LogFactory.getLog(CdiProducer.class);
    
    @ConfigProperty(name = "referentiel.path") String referentielPath;
    
    

    // ----------- Methode(s) -----------
    @Produces @LocalRegistryConfig
    public ParametreService getParametreService() 
    {
        ParametreService result = null;
        try
        {
            ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
            ResteasyClient client = builder.build();
            ResteasyWebTarget target = client.target(UriBuilder.fromPath(referentielPath+"/referentiel/api"));
            result = target.proxy(ParametreService.class);
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
}
