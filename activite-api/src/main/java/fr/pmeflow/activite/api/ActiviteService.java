/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.activite.api;

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
import fr.pmeflow.activite.model.Activite;
import fr.pmeflow.commons.model.BusEvent;

/**
 * Cette interface represente les fonctions disponibles dans le service Contact.
 * 
 * @author Mattecoco
 */
@Path(ActiviteService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
public interface ActiviteService extends Serializable {
	// ----------- Attribut(s) -----------
	static final long serialVersionUID = 202210181614L;
	static final String SERVICE_NAME = "pmeflow-service-activite";
	static final String SERVICE_VERSION = "v1";
	static final String SERVICE_URI = SERVICE_VERSION + "/activite";
	static final String FULL_SERVICE_URI = "/pmeflow/api/" + SERVICE_URI;
	static final String SERVICE_CODE = "ACTIVITESERVICE";

	// ----------- Methodes -----------
	@GET
	@Path("/chercherParOidCible")
	public List<Activite> chercherParOidCible(@QueryParam("oidCible") String oid) throws BusinessException;
	
	@GET
	@Path("/supprimer")
	public boolean supprimer(@QueryParam("oidActivite") String oid) throws BusinessException;
	
	@POST
	@Path("/modifier")
	public Activite modifier(Activite contact) throws BusinessException;

	boolean routeBusEvent(BusEvent event) throws BusinessException;
}