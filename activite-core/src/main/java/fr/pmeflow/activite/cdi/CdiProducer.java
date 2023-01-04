/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.activite.cdi;

import java.util.Optional;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;

/**
 * Cette classe assure la production des objets pour CDI
 * 
 * @author Ludovic.terral
 */
public class CdiProducer {
	// ----------- Attribut(s) -----------
	private static final Log LOG = LogFactory.getLog(CdiProducer.class);

	@Inject
	RabbitMQClient rabbitMQClient;

	@ConfigProperty(name = "redis.uri")
	String redisUri;

	// ----------- Methode(s) -----------
	@Produces
	@Named("pmeflow-eventbus")
	public Channel getEventBusChannel() {
		Channel result = null;
		try {
			Connection connection = rabbitMQClient.connect();
			Optional<Channel> oChannel = connection.openChannel();
			if (oChannel.isPresent()) {
				result = oChannel.get();
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage());
		}
		return result;
	}

	@Produces
	@Named("pmeflow-redis")
	public ExtendRedisClient getExtendRedisClient() {
		ExtendRedisClient result = null;
		try {
			if (StringUtil.isNotEmpty(redisUri)) {
				String hostName = redisUri.substring(6, redisUri.lastIndexOf(':'));
				String portRedis = redisUri.substring(redisUri.lastIndexOf(':') + 1);
				result = new ExtendRedisClient(hostName, Integer.parseInt(portRedis));
			}
		} catch (Exception e) {
			LOG.fatal(e.getMessage());
		}
		return result;
	}
}
