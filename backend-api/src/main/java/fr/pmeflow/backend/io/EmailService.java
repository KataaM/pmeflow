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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.backend.abonnement.model.Abonnement;
import fr.pmeflow.backend.dossier.model.Dossier;
import fr.pmeflow.backend.facturation.model.Facture;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.backend.rdv.model.RendezVous;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Email.
 * 
 * @author ludovic.terral
 */
@Path(EmailService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8", MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
public interface EmailService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 1306745024315L;
    static final String SERVICE_NAME = "pmeflow-service-api-email";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/email";
    static final String FULL_SERVICE_URI = "/backend/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "MAILSERV";
    
    

    // ----------- Methodes -----------
    @POST @Path("/{langue}/route") boolean routeBusEvent(@PathParam("langue") Langue langue, BusEvent event) throws BusinessException;
    
    
    @POST @Path("/{langue}/abonnement/finEngagement") boolean sendEmailAbonnementFinEngagement(@PathParam("langue") Langue langue, Abonnement abonnement) throws BusinessException;
        
    @POST @Path("/{langue}/dossier/created/{majClient}") boolean sendEmailDossierCreated(@PathParam("langue") Langue langue, Dossier dossier, @PathParam("majClient") boolean majClient, @QueryParam("emails") String[] emails) throws BusinessException;
    @POST @Path("/{langue}/dossier/updated/{majClient}") boolean sendEmailDossierUpdated(@PathParam("langue") Langue langue, Dossier dossier, @PathParam("majClient") boolean majClient, @QueryParam("emails") String[] emails) throws BusinessException;
    @POST @Path("/{langue}/dossier/to-be-completed") boolean sendEmailDossierToBeCompleted(@PathParam("langue") Langue langue, Dossier dossier) throws BusinessException;
    
    @POST @Path("/{langue}/facture") boolean sendEmailFactureReady(@PathParam("langue") Langue langue, Facture facture) throws BusinessException;
    
    @POST @Path("/{langue}/prestation/aRegler") boolean sendEmailPrestationARegler(@PathParam("langue") Langue langue, Prestation prestation) throws BusinessException;
    
    @POST @Path("/{langue}/rdv/confirmClient") boolean sendEmailRendezVousConfirmClient(@PathParam("langue") Langue langue, RendezVous rdv) throws BusinessException;
    @POST @Path("/{langue}/rdv/informJuriste") boolean sendEmailRendezVousInformJuriste(@PathParam("langue") Langue langue, RendezVous rdv) throws BusinessException;
}