/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.dossier;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.backend.dossier.model.Dossier;
import fr.pmeflow.backend.io.model.Document;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Dossier.
 * 
 * @author ludovic.terral
 */
@Path(DossierService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface DossierService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1306745024315L;
    static final String SERVICE_NAME = "pmeflow-service-api-dossier";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/dossier";
    static final String FULL_SERVICE_URI = "/backend/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "DOSSERV";
    
    

    // ----------- Methodes -----------
    @GET @Path("/{langue}/verif/create") boolean verifDossierCreate(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/all") List<Dossier> getDossiers(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/actif") List<Dossier> getDossiersActifs(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") Dossier getDossierById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}") Dossier mergeDossier(@PathParam("langue") Langue langue, Dossier dossier) throws BusinessException;
    @POST @Path("/{langue}/{id}/document") boolean addDocumentToDossier(@PathParam("langue") Langue langue, @PathParam("id") String id, Document document) throws BusinessException;
}
