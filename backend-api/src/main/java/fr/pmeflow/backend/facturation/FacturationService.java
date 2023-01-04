/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.facturation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pmeflow.backend.facturation.model.Facture;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Abonnement.
 * 
 * @author ludovic.terral
 */
@Path(FacturationService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface FacturationService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1306745024315L;
    static final String SERVICE_NAME = "pmeflow-service-api-facturation";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/facturation";
    static final String FULL_SERVICE_URI = SERVICE_URI;
    static final String SERVICE_CODE = "FACTUSERV";
    
    

    // ----------- Methodes -----------  
    @POST @Path("/{langue}/route") boolean routeBusEvent(@PathParam("langue") Langue langue, BusEvent event) throws BusinessException;

    @POST @Path("/{langue}/prestation/create") Facture createFactureForPrestation(@PathParam("langue") Langue langue, Prestation prestation) throws BusinessException;
    @GET @Path("/{langue}/prestation/{prestationId}") Response getFactureForPrestation(@PathParam("langue") Langue langue, @PathParam("prestationId") String prestationId) throws BusinessException;
}
