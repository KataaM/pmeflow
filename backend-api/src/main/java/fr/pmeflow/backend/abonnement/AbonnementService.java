/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.abonnement;

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

import fr.pmeflow.backend.abonnement.model.Abonnement;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Abonnement.
 * 
 * @author ludovic.terral
 */
@Path(AbonnementService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface AbonnementService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1306745024315L;
    static final String SERVICE_NAME = "pmeflow-service-api-abonnement";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/abonnement";
    static final String FULL_SERVICE_URI = "/backend/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "ABNSERV";
    
    

    // ----------- Methodes -----------  
    @GET @Path("/{langue}/prepare/{id}") Abonnement prepareAbonnement(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    
    @GET @Path("/{langue}/all") List<Abonnement> getAbonnements(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") Abonnement getAbonnementById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}/expression") List<Abonnement> getAbonnementsByExpression(@PathParam("langue") Langue langue, String query) throws BusinessException;
    @POST @Path("/{langue}") Abonnement mergeAbonnement(@PathParam("langue") Langue langue, Abonnement abonnement) throws BusinessException;
    @DELETE @Path("/{langue}/{id}") Abonnement resilierAbonnement(@PathParam("langue") Langue langue, @PathParam("id") String id, String motif) throws BusinessException;

    @GET @Path("/{langue}/mensualite") boolean facturerMensualite(@PathParam("langue") Langue langue, @QueryParam("refDay") int refDay) throws BusinessException;
}