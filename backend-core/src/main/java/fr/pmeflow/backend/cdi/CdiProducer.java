/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.cdi;

import java.util.Optional;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import fr.pmeflow.backend.abonnement.AbonnementService;
import fr.pmeflow.backend.dossier.DossierService;
import fr.pmeflow.backend.facturation.FacturationService;
import fr.pmeflow.backend.io.DocumentService;
import fr.pmeflow.backend.io.EmailService;
import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.rdv.RendezVousService;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.authentification.BearerAuthenticator;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.ClientService;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.referentiel.abonnement.RefAbonnementService;
import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.pmeflow.referentiel.prestation.RefPrestationService;
import fr.pmeflow.stripe.PaymentService;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.client.RegistryServiceClient;
import fr.lixbox.service.registry.model.ServiceEntry;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;

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
    private static final String BACKEND_API_PATH = "/backend/api/";
    private static final String HTTP_LOCALHOST_PATH = "http://localhost:";
    
    @ConfigProperty(name="registry.uri", defaultValue=DEFAULT_REGISTRY_URL) String registryUri;
    @ConfigProperty(name="stripe.service.uri") String stripeServiceUri;
    @ConfigProperty(name = "redis.uri") String redisUri;
    @ConfigProperty(name = "referentiel.services.uri") String referentielServicesUri;
    @ConfigProperty(name = "iam.services.uri") String iamServicesUri;
    
    @ConfigProperty(name = "quarkus.http.port") String port;
    
    @Inject RabbitMQClient rabbitMQClient;
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
    public PaymentService getPaymentService() 
    {
        PaymentService result = null;
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
                result = target.proxy(PaymentService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
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
                String hostName = redisUri.substring(6,redisUri.lastIndexOf(':'));
                String portRedis = redisUri.substring(redisUri.lastIndexOf(':')+1);
                result = new ExtendRedisClient(hostName, Integer.parseInt(portRedis));
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public RefAbonnementService getRefAbonnementService() 
    {
        RefAbonnementService result = null;
        try
        {
            if (StringUtil.isNotEmpty(referentielServicesUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(referentielServicesUri));
                result = target.proxy(RefAbonnementService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public RefPrestationService getRefPrestationService() 
    {
        RefPrestationService result = null;
        try
        {
            if (StringUtil.isNotEmpty(referentielServicesUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(referentielServicesUri));
                result = target.proxy(RefPrestationService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public ClientService getClientService() 
    {
        ClientService result = null;
        try
        {
            if (StringUtil.isNotEmpty(iamServicesUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(iamServicesUri));
                result = target.proxy(ClientService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("utilisateurService_doc-generator") 
    public UtilisateurService getUtilisateurServiceDocGenerator() 
    {
        UtilisateurService result = null;
        try
        {
            if (StringUtil.isNotEmpty(iamServicesUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(iamServicesUri));
                result = target.proxy(UtilisateurService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public UtilisateurService getUtilisateurService() 
    {
        UtilisateurService result = null;
        try
        {
            if (StringUtil.isNotEmpty(iamServicesUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(iamServicesUri));
                result = target.proxy(UtilisateurService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public DossierService getDossierServiceDocGenerator() 
    {
        DossierService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
            ResteasyClient client = builder.build();
            client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
            ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
            result = target.proxy(DossierService.class);
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("abonnementService-user-connected")
    public AbonnementService getAbonnementServiceUserNamed() 
    {
        AbonnementService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(AbonnementService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public AbonnementService getAbonnementService() 
    {
        AbonnementService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(AbonnementService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public PrestationService getPrestationService() 
    {
        PrestationService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(PrestationService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("prestationService-user-connected")
    public PrestationService getPrestationServiceUserNamed() 
    {
        PrestationService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                if (jwt.getClaimNames()!=null)
                {
                    client.register(new BearerAuthenticator(jwt.getRawToken()));
                }
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(PrestationService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("no_auth")
    public EmailService getEmailServiceNoAuth() 
    {
        EmailService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(EmailService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public EmailService getEmailService() 
    {
        EmailService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(EmailService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("RendezVousService_doc-generator")
    public RendezVousService getRendezVousServiceDocGenerator() 
    {
        RendezVousService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(RendezVousService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public FacturationService getFacturationService() 
    {
        FacturationService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(FacturationService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("documentService_doc-generator")
    public DocumentService getDocumentServiceDocGenerator() 
    {
        DocumentService result = null;
        try
        {
            String serviceUri = HTTP_LOCALHOST_PATH+port+BACKEND_API_PATH;
            if (StringUtil.isNotEmpty(serviceUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
                result = target.proxy(DocumentService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @Named("parametreService_doc-generator") 
    public ParametreService getParametreServiceDocGenerator() 
    {
        ParametreService result = null;
        try
        {
            if (StringUtil.isNotEmpty(referentielServicesUri))
            {
                ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                client.register(new BearerAuthenticator(getClientService().getDocGeneratorToken(Langue.FR_FR)));
                ResteasyWebTarget target = client.target(UriBuilder.fromPath(referentielServicesUri));
                result = target.proxy(ParametreService.class);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public Channel getEventBusChannel()
    {
        Channel result = null;
        try 
        {
            Connection connection = rabbitMQClient.connect();
            Optional<Channel> oChannel = connection.openChannel();
            if (oChannel.isPresent())
            {
                result = oChannel.get();
            }
        } 
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
}
