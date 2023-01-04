/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.opportunite.api;

import java.io.Serializable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.lixbox.common.exceptions.BusinessException;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.opportunite.model.Opportunite;

/**
 * Cette interface represente les fonctions disponibles dans le service Opportunite.
 * 
 * @author Corentin
 */
@Path(OpportuniteService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
public interface OpportuniteService extends Serializable {
	// ----------- Attribut(s) -----------
	static final long serialVersionUID = 2045820487481614L;
	static final String SERVICE_NAME = "pmeflow-service-opportunite";
	static final String SERVICE_VERSION = "v1";
	static final String SERVICE_URI = SERVICE_VERSION + "/opportunite";
	static final String FULL_SERVICE_URI = "/pmeflow/api/" + SERVICE_URI;
	static final String SERVICE_CODE = "OPPORTUNITESERVICE";

	// ----------- Methodes -----------
	
	
	@GET
	@Path("/lister")
	public List<Opportunite> lister() throws BusinessException;

	@GET
	@Path("/chercherParOid")
	public Opportunite chercherParOid(@QueryParam("oidContact") String oid) throws BusinessException;
	
	@GET
	@Path("/chercherParIdEntreprise")
	public List<Opportunite> chercherParIdEntreprise(@QueryParam("oidEntreprise") String oid) throws BusinessException;
	
	@GET
	@Path("/supprimer")
	public Opportunite supprimer(@QueryParam("oidContact") String oid) throws BusinessException;
	
	@POST
	@Path("/ajouter")
	public Opportunite ajouter(Opportunite opportunite) throws BusinessException;
	
	@POST
	@Path("/modifier")
	public Opportunite modifier(Opportunite opportunite) throws BusinessException;

	boolean routeBusEvent(BusEvent event) throws BusinessException;
}