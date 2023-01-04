/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import fr.pmeflow.backend.abonnement.model.AbonnementEventBusMetadata;
import fr.pmeflow.backend.dossier.model.DossierEventBusMetadata;
import fr.pmeflow.backend.facturation.FacturationService;
import fr.pmeflow.backend.facturation.model.FacturationEventBusMetadata;
import fr.pmeflow.backend.io.EmailService;
import fr.pmeflow.backend.io.model.EmailEventBusMetadata;
import fr.pmeflow.backend.prestation.model.PrestationEventBusMetadata;
import fr.pmeflow.backend.rdv.model.RendezVousEventBusMetadata;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

/**
 * Cette classe assure l'initialisation de l'application JAX-RS
 * des Services Backend.
 * 
 * @author ludovic.terral
 */
public class BackendInitializer implements Serializable 
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = 202205050925L;
    
    @Inject @LocalRegistryConfig Channel channel;
    @Inject @Named("no_auth") EmailService emailService;
    @Inject @LocalRegistryConfig FacturationService facturationService;

    
    
    // ----------- Methode(s) -----------
    public void onApplicationStart(@Observes StartupEvent event) throws BusinessException
    {
        setupBroker();
        setupEmailExchangeConsummer();
        setupFacturationExchangeConsummer();
    }
    
    
    
    private void setupBroker() throws BusinessException 
    {
        try 
        {
            //déclaration des exchanges
            channel.exchangeDeclare(AbonnementEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
            channel.exchangeDeclare(DossierEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
            channel.exchangeDeclare(FacturationEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
            channel.exchangeDeclare(PrestationEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
            channel.exchangeDeclare(RendezVousEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
            
            //déclaration des queues
            channel.queueDeclare(EmailEventBusMetadata.QUEUE, true, false, false, null);
            channel.queueDeclare(FacturationEventBusMetadata.QUEUE, true, false, false, null);
            
            //déclaration des routes
            channel.queueBind(EmailEventBusMetadata.QUEUE, AbonnementEventBusMetadata.EXCHANGE, AbonnementEventBusMetadata.EVENT_FIN_ENGAGEMENT);
            
            channel.queueBind(EmailEventBusMetadata.QUEUE, DossierEventBusMetadata.EXCHANGE, DossierEventBusMetadata.EVENT_DOSSIER_CREATED);
            channel.queueBind(EmailEventBusMetadata.QUEUE, DossierEventBusMetadata.EXCHANGE, DossierEventBusMetadata.EVENT_DOSSIER_TO_BE_COMPLETED);
            channel.queueBind(EmailEventBusMetadata.QUEUE, DossierEventBusMetadata.EXCHANGE, DossierEventBusMetadata.EVENT_DOSSIER_UPDATED);
            channel.queueBind(EmailEventBusMetadata.QUEUE, FacturationEventBusMetadata.EXCHANGE, FacturationEventBusMetadata.EVENT_FACTURE_READY);
            channel.queueBind(EmailEventBusMetadata.QUEUE, PrestationEventBusMetadata.EXCHANGE, PrestationEventBusMetadata.EVENT_PRESTATION_TO_PAY);
            
            channel.queueBind(EmailEventBusMetadata.QUEUE, RendezVousEventBusMetadata.EXCHANGE, RendezVousEventBusMetadata.EVENT_RDV_CONFIRM_TO_CLIENT);
            channel.queueBind(EmailEventBusMetadata.QUEUE, RendezVousEventBusMetadata.EXCHANGE, RendezVousEventBusMetadata.EVENT_RDV_INFORM_JURISTE);
            
            channel.queueBind(FacturationEventBusMetadata.QUEUE, PrestationEventBusMetadata.EXCHANGE, PrestationEventBusMetadata.EVENT_PRESTATION_PAYED);
            
        } 
        catch (Exception e)
        {
            Log.error(e);
            ExceptionUtil.traiterException(e, PmeflowConstant.PMEFLOW_CODE, false);
        }
    }
    
          
            
    private void setupEmailExchangeConsummer() throws BusinessException
    {
        try
        {
            channel.basicConsume(EmailEventBusMetadata.QUEUE, true, new DefaultConsumer(channel)
            {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
                {
                    try
                    {
                        BusEvent event = new BusEvent();
                        event.setName(envelope.getRoutingKey());
                        if (properties!=null && properties.getHeaders()!=null)
                        {
                            for (java.util.Map.Entry<String, Object> entry:properties.getHeaders().entrySet())
                            {
                                event.getProperties().put(entry.getKey(), entry.getValue()+"");
                            }
                        }
                        event.setBody(new String(body, StandardCharsets.UTF_8));
                        emailService.routeBusEvent(Langue.FR_FR, event);
                    }
                    catch (BusinessException e)
                    {
                        throw new IOException(e);
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.error(e);
            ExceptionUtil.traiterException(e, PmeflowConstant.PMEFLOW_CODE, false);
        }
    }
    
          
            
    private void setupFacturationExchangeConsummer() throws BusinessException
    {
        try
        {
            channel.basicConsume(FacturationEventBusMetadata.QUEUE, true, new DefaultConsumer(channel)
            {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
                {
                    try
                    {
                        BusEvent event = new BusEvent();
                        event.setName(envelope.getRoutingKey());
                        if (properties!=null && properties.getHeaders()!=null)
                        {
                            for (java.util.Map.Entry<String, Object> entry:properties.getHeaders().entrySet())
                            {
                                event.getProperties().put(entry.getKey(), entry.getValue()+"");
                            }
                        }
                        event.setBody(new String(body, StandardCharsets.UTF_8));
                        facturationService.routeBusEvent(Langue.FR_FR, event);
                    }
                    catch (BusinessException e)
                    {
                        throw new IOException(e);
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.error(e);
            ExceptionUtil.traiterException(e, PmeflowConstant.PMEFLOW_CODE, false);
        }
    }
}