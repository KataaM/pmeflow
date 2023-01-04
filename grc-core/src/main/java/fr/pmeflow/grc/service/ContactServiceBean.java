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
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.grc.api.ContactService;
import fr.pmeflow.activite.eventbus.ContactEventBusMetadata;
import fr.pmeflow.grc.model.Contact;

/**
 * Ce service assure les actions sur les contacts du GRC
 * 
 * @author Mattecoco
 */
@ApplicationScoped
public class ContactServiceBean implements ContactService {
	// ----------- Attribut(s) -----------
	private static final long serialVersionUID = 202210181620L;
	private static final Log LOG = LogFactory.getLog(ContactService.class);

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
//			case ContactEventBusMetadata.EVENT_CONTACT_ADDED:
//				LOG.info("Contact added.");
//				break;
//			case ContactEventBusMetadata.EVENT_CONTACT_UPDATED:
//				LOG.info("Contact updated.");
//				break;
//			case ContactEventBusMetadata.EVENT_CONTACT_DELETED:
//				LOG.info("Contact deleted.");
//				break;
//
//			default:
//				break;
//			}
//		} catch (Exception e) {
//			LOG.error(e);
//			ExceptionUtil.traiterException(e, ContactService.SERVICE_CODE, true);
//		}
		return result;
	}
	
	
	/**
	 * fonction privée qui permet d'ajouter/modifier un contact en BDD
	 * @param contact
	 * @return le contact ajouté ou modifié
	 * @throws BusinessException
	 */
	private Contact merge(Contact contact) throws BusinessException {
		Contact result = null;
		try {
			final Contexte contexte = new Contexte();
			ConteneurEvenement events = contact.validate(contact.getClass().getSimpleName().toLowerCase() + ".",
					contexte);
			if (!events.getEvenementTypeErreur().isEmpty()) {
				throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", Langue.FR_FR));
			}
			result = redisClient.merge(contact);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ContactService.SERVICE_CODE, true);
		}
		return result;
	}
	
	/**
	 * fonction privée qui permet de supprimer logiquement un contact
	 * en passant son champ etat à false
	 * @param oid - l'oid du contact a supprimer
	 * @return le contact logiquement supprimé avec son état a false
	 * @throws BusinessException
	 */
	private Contact delete(String oid) throws BusinessException {
		Contact result = null;
		try {
			Contact contactToDelete = chercherParOid(oid);
			contactToDelete.setEtatSupprime(true);
			
			result = redisClient.merge(contactToDelete);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ContactService.SERVICE_CODE, true);
		}
		return result;
	}
	

	/**
	 * Renvoie tous les contacts de la BDD redis
	 */
	@Override
	public List<Contact> listAll() throws BusinessException {

		List<Contact> results = new ArrayList<Contact>();

		try {
			String redisQuery = "*";
			results.addAll(redisClient.findByExpression(Contact.class, redisQuery));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
		}

		return results;
	}
	
	
	/**
	 * Renvoie tous les contacts actifs de la BDD redis (ceux non-supprimés)
	 */
	@Override
	public List<Contact> listNotDeleted() throws BusinessException {

		List<Contact> results = new ArrayList<Contact>();

		try {
			EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("etatSupprime", "false"));
			results.addAll(redisClient.findByExpression(Contact.class, query));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
		}

		return results;
	}

	/**
	 * Effectue une recherche dans la BDD redis et retourne le
	 * contact avec l'oid spécifié, s'il n'existe pas une erreur est lancée
	 */
	@Override
	public Contact chercherParOid(String oid) throws BusinessException {
		Contact result = null;

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			result = redisClient.findById(Contact.class, oid);
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
		}

		return result;
	}
	
	/**
	 * Effectue une recherche dans la BDD redis et retourne tous les contacts
	 * qui travaille dans une entreprise spécifiée en paramètre
	 */
	@Override
	public List<Contact> chercherParIdEntreprise(String oid) throws BusinessException {
		List<Contact> results = new ArrayList<Contact>();

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("entrepriseId", oid));
			results.addAll(redisClient.findByExpression(Contact.class, query));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
		}

		return results;
	}

	@Override
	public Contact ajouter(Contact contact) throws BusinessException {
		Contact result = merge(contact);
		if (result != null) {
			try {
				channel.basicPublish(ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_ADDED, null, result.toString().getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
			}
		}
		
		return result;
	}
	
	@Override
	public Contact modifier(Contact contact) throws BusinessException {
		Contact result = merge(contact);
		if (result != null) {
			try {
				channel.basicPublish(ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_UPDATED, null, result.toString().getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
			}
		}
		
		return result;
	}
	
	@Override
	public Contact supprimer(String oid) throws BusinessException {
		Contact result = delete(oid);
		if (result != null) {
			try {
				channel.basicPublish(ContactEventBusMetadata.EXCHANGE, ContactEventBusMetadata.EVENT_CONTACT_DELETED, null, result.toString().getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				ExceptionUtil.traiterException(e, ContactServiceBean.SERVICE_CODE, false);
			}
		}
		
		return result;
	}
}