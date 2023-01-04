/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.io;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fr.pmeflow.backend.facturation.model.Facture;
import fr.pmeflow.backend.io.model.StorageMultiPartBody;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Document.
 * 
 * @author ludovic.terral
 */
@Path(DocumentService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8", MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
public interface DocumentService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1306745024315L;
    static final String SERVICE_NAME = "pmeflow-service-api-document";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/document";
    static final String FULL_SERVICE_URI = "/backend/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "DOSSERV";
    
    

    // ----------- Methodes -----------     
    @POST @Path("/{langue}/facture/generate") byte[] generateFacture(@PathParam("langue") Langue langue, Facture facture) throws BusinessException;
    
    @GET @Path("/{langue}/{id}") Response getDocumentById(@PathParam("langue") Langue langue, @PathParam("id") String id, @QueryParam("access_token") String accessToken) throws BusinessException;
    
    @GET @Path("/{langue}/generate/id") String getIdForDocument(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/storage/content/{id}") byte[] getDocumentStorageById(@PathParam("langue") Langue langue, @PathParam("id") String id, @QueryParam("secretKey") String secretKey) throws BusinessException;
    @POST @Path("/{langue}/storage/{id}") boolean mergeDocumentStorage(@PathParam("langue") Langue langue, @PathParam("id") String id, @MultipartForm StorageMultiPartBody form) throws BusinessException;


    
}