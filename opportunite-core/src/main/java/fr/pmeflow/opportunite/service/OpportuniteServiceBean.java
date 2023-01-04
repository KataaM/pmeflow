/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.opportunite.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Channel;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.opportunite.api.OpportuniteService;
import fr.pmeflow.opportunite.model.Opportunite;

/**
 * Ce service assure les actions sur les opportunites du GRC
 * 
 * @author Corentin
 */
@ApplicationScoped
public class OpportuniteServiceBean implements OpportuniteService {
	// ----------- Attribut(s) -----------
	private static final long serialVersionUID = 20685416356L;
	private static final Log LOG = LogFactory.getLog(OpportuniteService.class);

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
//			case opportuniteEventBusMetadata.EVENT_opportunite_ADDED:
//				LOG.info("opportunite added.");
//				break;
//			case opportuniteEventBusMetadata.EVENT_opportunite_UPDATED:
//				LOG.info("opportunite updated.");
//				break;
//			case opportuniteEventBusMetadata.EVENT_opportunite_DELETED:
//				LOG.info("opportunite deleted.");
//				break;
//
//			default:
//				break;
//			}
//		} catch (Exception e) {
//			LOG.error(e);
//			ExceptionUtil.traiterException(e, opportuniteService.SERVICE_CODE, true);
//		}
		return result;
	}
	
	
	
	
	
	/**
	 * fonction privée qui permet d'ajouter/modifier un opportunite en BDD
	 * @param opportunite
	 * @return le opportunite ajouté ou modifié
	 * @throws BusinessException
	 */
	/*
	private Opportunite merge(Opportunite opportuntie) throws BusinessException {
		opportunite result = null;
		try {
			final Contexte contexte = new Contexte();
			ConteneurEvenement events = opportunite.validate(opportunite.getClass().getSimpleName().toLowerCase() + ".",
					contexte);
			if (!events.getEvenementTypeErreur().isEmpty()) {
				throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", Langue.FR_FR));
			}
			result = redisClient.merge(opportunite);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, opportuniteService.SERVICE_CODE, true);
		}
		return result;
	}
	
	/**
	 * fonction privée qui permet de supprimer logiquement un opportunite
	 * en passant son champ etat à false
	 * @param oid - l'oid du opportunite a supprimer
	 * @return le opportunite logiquement supprimé avec son état a false
	 * @throws BusinessException
	 */
	/*
	private opportunite delete(String oid) throws BusinessException {
		opportunite result = null;
		try {
			opportunite opportuniteToDelete = chercherParOid(oid);
			opportuniteToDelete.setEtat(false);
			
			result = redisClient.merge(opportuniteToDelete);

		} catch (Exception e) {
			ExceptionUtil.traiterException(e, opportuniteService.SERVICE_CODE, true);
		}
		return result;
	}
	*/

	/**
	 * Renvoie tous les opportunites de la BDD redis
	 */
	/*
	@Override
	public List<opportunite> lister() throws BusinessException {

		List<opportunite> results = new ArrayList<opportunite>();

		try {
			String redisQuery = "*";
			results.addAll(redisClient.findByExpression(opportunite.class, redisQuery));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, opportuniteServiceBean.SERVICE_CODE, false);
		}

		return results;
	}
*/
	/**
	 * Effectue une recherche dans la BDD redis et retourne le
	 * opportunite avec l'oid spécifié, s'il n'existe pas une erreur est lancée
	 *//*
	@Override
	public opportunite chercherParOid(String oid) throws BusinessException {
		opportunite result = null;

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			result = redisClient.findById(opportunite.class, oid);
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, opportuniteServiceBean.SERVICE_CODE, false);
		}

		return result;
	}*/
	
	/**
	 * Effectue une recherche dans la BDD redis et retourne tous les opportunites
	 * qui travaille dans une entreprise spécifiée en paramètre
	 */
	/*
	@Override
	public List<opportunite> chercherParIdEntreprise(String oid) throws BusinessException {
		List<opportunite> results = new ArrayList<opportunite>();

		// Controler les parametres
		if (StringUtil.isEmpty(oid)) {
			throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_02", Langue.FR_FR));
		}

		try {
			EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("entrepriseId", oid));
			results.addAll(redisClient.findByExpression(opportunite.class, query));
		} catch (Exception e) {
			ExceptionUtil.traiterException(e, opportuniteServiceBean.SERVICE_CODE, false);
		}

		return results;
	}
*/

	@Override
	public Opportunite supprimer(String oid) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Opportunite ajouter(Opportunite opportunite) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Opportunite modifier(Opportunite opportunite) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	public List<Opportunite> lister() throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	public Opportunite chercherParOid(String oid) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	public List<Opportunite> chercherParIdEntreprise(String oid) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	
}