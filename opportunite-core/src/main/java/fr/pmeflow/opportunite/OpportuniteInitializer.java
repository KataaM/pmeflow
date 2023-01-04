/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.opportunite;

import java.io.Serializable;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.util.ExceptionUtil;
import fr.pmeflow.activite.eventbus.OpportuniteEventBusMetadata;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.opportunite.api.OpportuniteService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

/**
 * Cette classe assure l'initialisation de l'application Opportunite.
 * 
 * @author Corentin
 */
@Singleton
public class OpportuniteInitializer implements Serializable {
	// ----------- Attribut(s) -----------
	private static final long serialVersionUID = 202205050925L;

	@Inject
	@Named("pmeflow-eventbus")
	Channel channel;

	@Inject	OpportuniteService OpportuniteService;

	// ----------- Methode(s) -----------
	public void onApplicationStart(@Observes StartupEvent event) throws BusinessException {
		setupBroker();
	}

	private void setupBroker() throws BusinessException {
		try {
			// déclaration des exchanges
			channel.exchangeDeclare(OpportuniteEventBusMetadata.EXCHANGE, BuiltinExchangeType.DIRECT, true);

			// déclaration des queues
			// rien à déclarer de ce coté

			// déclaration des routes
			// rien à déclarer de ce coté

		} catch (Exception e) {
			Log.error(e);
			ExceptionUtil.traiterException(e, PmeflowConstant.PMEFLOW_CODE, false);
		}
	}
}