/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.grc.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Channel;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.model.Contexte;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.model.EQuery;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.pmeflow.activite.eventbus.ContactEventBusMetadata;
import fr.pmeflow.activite.eventbus.EntrepriseEventBusMetadata;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.grc.api.EntrepriseService;
import fr.pmeflow.grc.model.Contact;
import fr.pmeflow.grc.model.Entreprise;

/**
 * Ce service assure les actions sur les entreprises du GRC
 * 
 * @author Mattecoco
 */
@ApplicationScoped
public class EntrepriseServiceBean implements EntrepriseService {
	// ----------- Attribut(s) -----------
	private static final long serialVersionUID = 362210191520L;
	private static final Log LOG = LogFactory.getLog(EntrepriseService.class);

	@Inject
	@Named("pmeflow-eventbus")
	Channel channel;

	@Inject
	@Named("pmeflow-redis")
	ExtendRedisClient redisClient;

	// ----------- methods -----------
	@Override
	public boolean routeBusEvent(BusEvent event) throws BusinessException {
		boolean result = true;

		// TODO: ajouter des traitements aux évenements si besoin
//		try {
//			switch (event.getName()) {
//			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_ADDED:
//				LOG.info("Contact added.");
//				break;
//			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_UPDATED:
//				LOG.info("Contact updated.");
//				break;
//			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_DELETED:
//				LOG.info("Contact deleted.");
//				break;
//
//			default:
//				break;
//			}
//		} catch (Exception e) {
//			LOG.error(e);
//			ExceptionUtil.traiterException(e, EntrepriseService.SERVICE_CODE, true);
//		}
		return result;
	}
	
	
	/**
	 * fonction privée qui permet d'ajouter/modifier une entreprise en BDD
	 * @param entreprise
	 * @return l'entreprise ajoutée ou modifiée
	 * @throws BusinessException
	 */
	private Entreprise merge(Entreprise entreprise) throws BusinessException {
		Entreprise result = null;
		try {
			final Contexte contexte = new Contexte();
			ConteneurEvenement events = entreprise.validate(entreprise.getClass().getSimpleName().toLowerCase() + ".",
					contexte);
			if (!events.getEvenementTypeErreur().isEmpty()) {
				throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", Langue.FR_FR));
			}
			result = redisClient.merge(entreprise);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, EntrepriseService.SERVICE_CODE, true);
		}
		return result;
	}
	
	/**
	 * fonction privée qui permet de supprimer logiquement une entreprise
	 * en passant son champ etat à false
	 * @param oid - l'oid de l'entreprise a supprimer
	 * @return l'entreprise logiquement supprimée avec son état a false
	 * @throws BusinessException
	 */
	private Entreprise delete(String oid) throws BusinessException {
		Entreprise result = null;
		try {
			Entreprise companyToDelete = chercherParOid(oid);
			companyToDelete.setEtat(false);
			
			result = redisClient.merge(companyToDelete);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, EntrepriseService.SERVICE_CODE, true);
		}
		return result;
	}
	

	/**
	 * Renvoie toutes les entreprises de la BDD redis
	 */
	@Override
	public List<Entreprise> lister() throws BusinessException {

		List<Entreprise> results = new ArrayList<Entreprise>();

		try {
			String redisQuery = "*";
			results.addAll(redisClient.findByExpression(Entreprise.class, redisQuery));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, EntrepriseServiceBean.SERVICE_CODE, false);
		}

		return results;
	}

	/**
	 * Effectue une recherche dans la BDD redis et retourne l'entreprise
	 * avec l'oid spécifié, s'il n'existe pas une erreur est lancée
	 */
	@Override
	public Entreprise chercherParOid(String oid) throws BusinessException {
		Entreprise result = null;

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			result = redisClient.findById(Entreprise.class, oid);
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, EntrepriseServiceBean.SERVICE_CODE, false);
		}

		return result;
	}

	@Override
	public Entreprise ajouter(Entreprise entreprise) throws BusinessException {
		Entreprise result = merge(entreprise);
		if (result != null) {
			try {
				channel.basicPublish(EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_ADDED, null, result.toString().getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
			}
		}
		
		return result;
	}
	
	@Override
	public Entreprise modifier(Entreprise entreprise) throws BusinessException {
		Entreprise result = merge(entreprise);
		if (result != null) {
			try {
				channel.basicPublish(EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_UPDATED, null, result.toString().getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
			}
		}
		
		return result;
	}
	
	@Override
	public Entreprise supprimer(String oid) throws BusinessException {
		Entreprise result = delete(oid);
		if (result != null) {
			try {
				channel.basicPublish(EntrepriseEventBusMetadata.EXCHANGE, EntrepriseEventBusMetadata.EVENT_ENTREPRISE_DELETED, null, result.toString().getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
			}
		}
		
		return result;
	}
}