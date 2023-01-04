/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.facturation.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;

import fr.pmeflow.backend.abonnement.AbonnementService;
import fr.pmeflow.backend.dossier.DossierService;
import fr.pmeflow.backend.facturation.FacturationService;
import fr.pmeflow.backend.facturation.model.FacturationEventBusMetadata;
import fr.pmeflow.backend.facturation.model.Facture;
import fr.pmeflow.backend.io.DocumentService;
import fr.pmeflow.backend.io.EmailService;
import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.backend.prestation.model.PrestationEventBusMetadata;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.pmeflow.referentiel.parametre.model.Parametre;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;

/**
 * Ce service implémente le référentiel des documents.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@PermitAll
public class FacturationServiceBean implements FacturationService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202203010913L;
    private static final Log LOG = LogFactory.getLog(FacturationService.class);
    
    @Inject @LocalRegistryConfig Channel channel;
    
    @Inject @LocalRegistryConfig AbonnementService abonnementService;
    @Inject @Named("documentService_doc-generator") DocumentService documentService;
    @Inject @Named("parametreService_doc-generator") ParametreService parametreService;
    @Inject @LocalRegistryConfig PrestationService prestationService;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    
    
    
    
    
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
    @PermitAll
    public boolean routeBusEvent(Langue langue, BusEvent event) throws BusinessException
    {
        boolean result = true;
        try 
        {
            switch(event.getName())
            {
                case PrestationEventBusMetadata.EVENT_PRESTATION_TO_PAY:
                    channel.basicPublish(FacturationEventBusMetadata.EXCHANGE, event.getName(), null, event.getBody().getBytes(StandardCharsets.UTF_8));
                    break;
                case PrestationEventBusMetadata.EVENT_PRESTATION_PAYED:
                    Prestation prestation = JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Prestation>() {});
                    Facture facture = createFactureForPrestation(langue, prestation);
                    channel.basicPublish(FacturationEventBusMetadata.EXCHANGE, FacturationEventBusMetadata.EVENT_FACTURE_READY, null, facture.toString().getBytes(StandardCharsets.UTF_8));
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            LOG.error(e);
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, true);            
        }
        return result;
    }



    public Facture createFactureForPrestation(Langue langue, Prestation prestation) throws BusinessException
    {
        Facture facture = new Facture();
        try
        {
            facture.setClient(prestation.getClient());
            facture.setDate(Date.from(Instant.now()));
            facture.setPrestation(prestation);
            
            Parametre numFacture = parametreService.getParametreByCode("pmeflow", "NUM_FACTURE");
            Integer nextNum = Integer.valueOf(numFacture.getValue())+1;
            numFacture.setValue(nextNum+"");
            facture.setNumero(nextNum+"");            
            Parametre vatParam = parametreService.getParametreByCode("pmeflow", "VAT_RATE");
            facture.setTva(Double.valueOf(vatParam.getValue()));
            facture.setTotalTTC(prestation.getPrixHT()*(100+facture.getTva())/100);
            
            ConteneurEvenement events = facture.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {FacturationService.SERVICE_CODE}, langue), events);
            }
            facture = redisClient.merge(facture);
            parametreService.synchronize(numFacture);
            channel.basicPublish(FacturationEventBusMetadata.EXCHANGE, FacturationEventBusMetadata.EVENT_FACTURE_READY, null, facture.toString().getBytes(StandardCharsets.UTF_8));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, FacturationService.SERVICE_CODE, true);
        }
        return facture;
    }



    @Override
    @PermitAll
    public Response getFactureForPrestation(Langue langue, String prestationId) throws BusinessException
    {
        Response response = null;
        try
        {
            StringBuilder query = new StringBuilder();
            query.append(RedisSearchQueryHelper.toTextField("prestation_id", prestationId));   
            Facture facture = redisClient.findByExpression(Facture.class, query.toString()).get(0);
            byte[] document = documentService.generateFacture(langue, facture);
            response = Response
            .ok(document, MediaType.APPLICATION_OCTET_STREAM)
            .header("content-disposition","attachment; filename = facture-"+facture.getNumero()+".pdf").build();
        }
        catch (Exception e)
        {
            response = Response.noContent().build();
            ExceptionUtil.traiterException(e, DossierService.SERVICE_CODE, false);
        }        
        return response;
    }
}