/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.grc.api;

import java.io.Serializable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.lixbox.common.exceptions.BusinessException;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.grc.model.Entreprise;

/**
 * Cette interface represente les fonctions disponibles dans le service Entreprise.
 * 
 * @author Mattecoco
 */
@Path(EntrepriseService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
public interface EntrepriseService extends Serializable {
	// ----------- Attribut(s) -----------
	static final long serialVersionUID = 302540181614L;
	static final String SERVICE_NAME = "pmeflow-service-entreprise";
	static final String SERVICE_VERSION = "v1";
	static final String SERVICE_URI = SERVICE_VERSION + "/entreprise";
	static final String FULL_SERVICE_URI = "/pmeflow/api/" + SERVICE_URI;
	static final String SERVICE_CODE = "ENTREPRISESERVICE";

	// ----------- Methodes -----------
	@GET
	@Path("/lister")
	public List<Entreprise> lister() throws BusinessException;

	@GET
	@Path("/chercherParOid")
	public Entreprise chercherParOid(@QueryParam("oidEntreprise") String oid) throws BusinessException;
	
	@DELETE
	@Path("/supprimer")
	public Entreprise supprimer(@QueryParam("oidEntreprise") String oid) throws BusinessException;
	
	@POST
	@Path("/ajouter")
	public Entreprise ajouter(Entreprise entreprise) throws BusinessException;
	
	@POST
	@Path("/modifier")
	public Entreprise modifier(Entreprise entreprise) throws BusinessException;

	boolean routeBusEvent(BusEvent event) throws BusinessException;
}