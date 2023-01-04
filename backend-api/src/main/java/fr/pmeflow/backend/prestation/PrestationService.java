/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.prestation;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Prestation.
 * 
 * @author ludovic.terral
 */
@Path(PrestationService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface PrestationService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1306745024315L;
    static final String SERVICE_NAME = "pmeflow-service-api-prestation";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/prestation";
    static final String FULL_SERVICE_URI = "/backend/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "PRESTASERV";
    
    

    // ----------- Methodes -----------  
    @GET @Path("/{langue}/prepare/{id}") Prestation preparePrestation(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;

    @GET @Path("/{langue}/all") List<Prestation> getPrestations(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") Prestation getPrestationById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @GET @Path("/{langue}/abonnement/{id}") List<Prestation> getPrestationsByAbonnement(@PathParam("langue") Langue langue, @PathParam("id") String abonnementId) throws BusinessException;
    @GET @Path("/{langue}/dossier/{id}") List<Prestation> getPrestationsByDossier(@PathParam("langue") Langue langue, @PathParam("id") String dossierId) throws BusinessException;
    @POST @Path("/{langue}/expression") List<Prestation> getPrestationsByExpression(@PathParam("langue") Langue langue, String query) throws BusinessException;
    @POST @Path("/{langue}") Prestation mergePrestation(@PathParam("langue") Langue langue, Prestation prestation) throws BusinessException;
    @DELETE @Path("/{langue}/{id}") boolean removePrestation(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;

}