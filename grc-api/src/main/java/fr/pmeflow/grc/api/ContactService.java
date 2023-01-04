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
import fr.pmeflow.grc.model.Contact;

/**
 * Cette interface represente les fonctions disponibles dans le service Contact.
 * 
 * @author Mattecoco
 */
@Path(ContactService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON + "; charset=utf-8" })
public interface ContactService extends Serializable {
	// ----------- Attribut(s) -----------
	static final long serialVersionUID = 202210181614L;
	static final String SERVICE_NAME = "pmeflow-service-contact";
	static final String SERVICE_VERSION = "v1";
	static final String SERVICE_URI = SERVICE_VERSION + "/contact";
	static final String FULL_SERVICE_URI = "/pmeflow/api/" + SERVICE_URI;
	static final String SERVICE_CODE = "CONTACTSERVICE";

	// ----------- Methodes -----------
	@GET
	@Path("/listAll")
	public List<Contact> listAll() throws BusinessException;
	
	@GET
	@Path("/listNotDeleted")
	public List<Contact> listNotDeleted() throws BusinessException;

	@GET
	@Path("/chercherParOid")
	public Contact chercherParOid(@QueryParam("oidContact") String oid) throws BusinessException;
	
	@GET
	@Path("/chercherParIdEntreprise")
	public List<Contact> chercherParIdEntreprise(@QueryParam("oidEntreprise") String oid) throws BusinessException;
	
	@DELETE
	@Path("/supprimer")
	public Contact supprimer(@QueryParam("oidContact") String oid) throws BusinessException;
	
	@POST
	@Path("/ajouter")
	public Contact ajouter(Contact contact) throws BusinessException;
	
	@POST
	@Path("/modifier")
	public Contact modifier(Contact contact) throws BusinessException;

	boolean routeBusEvent(BusEvent event) throws BusinessException;
}