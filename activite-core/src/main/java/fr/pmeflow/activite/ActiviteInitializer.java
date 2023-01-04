/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.activite;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.util.ExceptionUtil;
import fr.pmeflow.activite.api.ActiviteService;
import fr.pmeflow.activite.eventbus.ActiviteEventBusMetadata;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.activite.eventbus.ContactEventBusMetadata;
import fr.pmeflow.activite.eventbus.EntrepriseEventBusMetadata;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

/**
 * Cette classe assure l'initialisation de l'application GRC.
 * 
 * @author Mattecoco
 */
@Singleton
public class ActiviteInitializer implements Serializable {
	// ----------- Attribut(s) -----------
	private static final long serialVersionUID = 202205050925L;

	@Inject
	@Named("pmeflow-eventbus")
	Channel channel;

	@Inject	ActiviteService ActiviteService;

	// ----------- Methode(s) -----------
	public void onApplicationStart(@Observes StartupEvent event) throws BusinessException {
		setupBroker();
		setupActiviteExchangeConsummer();
	}

	private void setupBroker() throws BusinessException {
		try {
			// déclaration des exchanges
			channel.exchangeDeclare(ContactEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
			channel.exchangeDeclare(EntrepriseEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
			channel.exchangeDeclare(ActiviteEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);
			
			// déclaration des queues
			channel.queueDeclare(ActiviteEventBusMetadata.QUEUE, true, false, false, null);

			// déclaration des routes
			/* redirection des evenements contacts */
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_ADDED);
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_UPDATED);
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_DELETED);
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_ENTREPRISE_LINKED);
			
			/* redirection des evenements entreprises */
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_ADDED);
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_UPDATED);
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_DELETED);
			channel.queueBind(ActiviteEventBusMetadata.QUEUE, EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_CONTACT_LINKED);

		} catch (Exception e) {
			Log.error(e);
			ExceptionUtil.traiterException(e, PmeflowConstant.PMEFLOW_CODE, false);
		}
	}
	
	
	private void setupActiviteExchangeConsummer() throws BusinessException
    {
        try
        {
            channel.basicConsume(ActiviteEventBusMetadata.QUEUE, true, new DefaultConsumer(channel)
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
                        ActiviteService.routeBusEvent(event);
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