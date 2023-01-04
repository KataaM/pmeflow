/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.rdv;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.backend.rdv.model.Evenement;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface définit le contrat du service evenement.
 *
 * @author ludovic.terral
 */
@Path(EvenementService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface EvenementService extends MicroService
{
	// ----------- Attribut -----------
	static final long serialVersionUID = 202204232331L;
    static final String SERVICE_NAME = "pmeflow-service-api-evenement";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/evenement";
    static final String FULL_SERVICE_URI = "/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "EVTSERV";
    
    

	// ----------- Methode -----------
    @GET @Path("/{langue}/all") List<Evenement> getEvenements(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") Evenement getEvenementById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}") Evenement mergeEvenement(@PathParam("langue") Langue langue, Evenement event) throws BusinessException;
    @DELETE @Path("/{langue}/{id}") boolean removeEvenement(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
}