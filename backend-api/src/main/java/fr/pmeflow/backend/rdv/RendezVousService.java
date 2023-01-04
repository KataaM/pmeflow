/*******************************************************************************
 *    
utilisateur *                           APPLICATION Pmeflow
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.backend.rdv.model.RendezVous;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface définit le contrat du service Rendez-vous.
 *
 * @author ludovic.terral
 */
@Path(RendezVousService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface RendezVousService extends MicroService
{
	// ----------- Attribut -----------
	static final long serialVersionUID = 202204232331L;

    static String SERVICE_NAME = "pmeflow-service-api-rendez-vous";
    static String SERVICE_CODE = "RDVSERV";
    static String SERVICE_VERSION = "1.0";
    static String SERVICE_URI = SERVICE_VERSION+"/rendez-vous";
    static String FULL_SERVICE_URI = "/api/"+SERVICE_URI;
        

	
	// ----------- Methode -----------
    @GET @Path("/{langue}/all") List<RendezVous> getRendezVous(@PathParam("langue") Langue langue) throws BusinessException;
    @POST @Path("/{langue}/expression") List<RendezVous> getRendezVousByExpression(@PathParam("langue") Langue langue, String expression) throws BusinessException;
    @GET @Path("/{langue}/actifs") List<RendezVous> getRendezVousActifs(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/dossier/{dossierId}") List<RendezVous> getRendezVousForDossier(@PathParam("langue") Langue langue, @PathParam("dossierId") String dossierId) throws BusinessException;
    @SuppressWarnings("rawtypes")
    @GET @Path("/{langue}/juriste/{juristeId}/dispo") List[] determinerRdvsPossiblesForJuriste(@PathParam("langue") Langue langue, @QueryParam("start") long start, @PathParam("juristeId") String juristeId);
    @GET @Path("/{langue}/{id}") RendezVous getRendezVousById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}") RendezVous mergeRendezVous(@PathParam("langue") Langue langue, RendezVous rdv) throws BusinessException;
    @DELETE @Path("/{langue}/{id}") boolean annuler(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
}