/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.activite.service;

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
import fr.pmeflow.activite.api.ActiviteService;
import fr.pmeflow.activite.model.Activite;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.activite.eventbus.ContactEventBusMetadata;
import fr.pmeflow.activite.eventbus.EntrepriseEventBusMetadata;

/**
 * Ce service assure les actions sur les contacts du GRC
 * 
 * @author Mattecoco
 */
@ApplicationScoped
public class ActiviteServiceBean implements ActiviteService {
	// ----------- Attribut(s) -----------
	private static final long serialVersionUID = 202210181620L;
	private static final Log LOG = LogFactory.getLog(ActiviteService.class);

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

		try {
			switch (event.getName()) {
			/* EVENTS CONTACTS */
			case ContactEventBusMetadata.EVENT_CONTACT_ADDED:
				LOG.info("Contact added.");
				break;
			case ContactEventBusMetadata.EVENT_CONTACT_UPDATED:
				LOG.info("Contact updated.");
				break;
			case ContactEventBusMetadata.EVENT_CONTACT_DELETED:
				LOG.info("Contact deleted.");
				break;
			case ContactEventBusMetadata.EVENT_CONTACT_ENTREPRISE_LINKED:
				LOG.info("Entreprise linked to Contact.");
				break;
				
			/* EVENTS ENTREPRISES */
			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_ADDED:
				LOG.info("Entreprise added.");
				break;
			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_UPDATED:
				LOG.info("Entreprise updated.");
				break;
			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_DELETED:
				LOG.info("Entreprise deleted.");
				break;
			case EntrepriseEventBusMetadata.EVENT_ENTREPRISE_CONTACT_LINKED:
				LOG.info("Contact linked to entreprise.");
				break;

			default:
				break;
			}
		} catch (Exception e) {
			LOG.error(e);
			ExceptionUtil.traiterException(e, ActiviteService.SERVICE_CODE, true);
		}
		return result;
	}
	
	
	/**
	 * fonction privée qui permet d'ajouter/modifier une activité en BDD
	 * @param activite
	 * @return l'activité ajoutée ou modifiée
	 * @throws BusinessException
	 */
	private Activite merge(Activite activite) throws BusinessException {
		Activite result = null;
		try {
			final Contexte contexte = new Contexte();
			ConteneurEvenement events = activite.validate(activite.getClass().getSimpleName().toLowerCase() + ".",
					contexte);
			if (!events.getEvenementTypeErreur().isEmpty()) {
				throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", Langue.FR_FR));
			}
			result = redisClient.merge(activite);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ActiviteService.SERVICE_CODE, true);
		}
		return result;
	}
	
	
	/**
	 * fonction privée qui permet de chercher une activite en bdd redis par son oid
	 * @param oid
	 * @return l'activite recherchee si succes, ou null le cas echeant
	 * @throws BusinessException
	 */
	private Activite chercherParOid(String oid) throws BusinessException {
		Activite result = null;

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			result = redisClient.findById(Activite.class, oid);
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ActiviteServiceBean.SERVICE_CODE, false);
		}

		return result;
	}
	
	/**
	 * fonction privée qui permet de supprimer une activité de la BDD redis
	 * @param oid - l'oid de l'activité a supprimer
	 * @return true si activité supprimée, sinon false
	 * @throws BusinessException
	 */
	private boolean delete(String oid) throws BusinessException {
		boolean success = false;
		try {
			Activite activiteToDelete = chercherParOid(oid);
			
			success = redisClient.remove(activiteToDelete.getKey());

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ActiviteService.SERVICE_CODE, true);
		}
		return success;
	}


	/**
	 * Effectue une recherche dans la BDD redis et retourne toutes
	 * les activites liées à une cible (Entreprise, Opportunité, Projet...)
	 */
	@Override
	public List<Activite> chercherParOidCible(String oid) throws BusinessException {
		List<Activite> results = new ArrayList<Activite>();

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("oidCible", oid));
			results.addAll(redisClient.findByExpression(Activite.class, query));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, ActiviteServiceBean.SERVICE_CODE, false);
		}

		return results;
	}
	
	@Override
	public Activite modifier(Activite activite) throws BusinessException {
		return merge(activite);
	}
	
	@Override
	public boolean supprimer(String oid) throws BusinessException {
		return delete(oid);
	}
	
	
}