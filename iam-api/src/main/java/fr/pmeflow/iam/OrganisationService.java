/*******************************************************************************
 *    
 *                           APPLICATION PMEFLOW
 *                          =====================
 *                          
 *   Copyright (C) 2002 Lixtec-Ludovic TERRAL
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   @AUTHOR Lixtec-Ludovic TERRAL
 *
 ******************************************************************************/
package fr.pmeflow.iam;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Organisation;
import fr.pmeflow.iam.model.Utilisateur;

/**
 * Cette interface represente les methodes accessibles a distance
 * concernant les organisations (entreprise, etablissement, structure interne).
 *
 * @author ludovic.terral
 */
@Path(OrganisationService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public interface OrganisationService extends MicroService
{
	// ----------- Attribut -----------
	static final long serialVersionUID = 201705120951L;

    static final String SERVICE_NAME = "pmeflow-service-api-organisation";
    static final String SERVICE_CODE = "ORGMGTSERV";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/organisation";
    static final String FULL_SERVICE_URI = "/iam/api/"+SERVICE_URI;



	// ----------- Methode -----------
    @GET @Path("/{langue}/{identifiant}/utilisateurs") List<Utilisateur> getUtilisateursByOrganisation(@PathParam("langue") Langue langue, @PathParam("identifiant") String identifiant) throws BusinessException;
    @GET @Path("/{langue}/{identifiant}") Organisation getOrganisationByIdentifiant(@PathParam("langue") Langue langue, @PathParam("identifiant") String identifiant) throws BusinessException;
    
    
    @APIResponses(
            value = {
                @APIResponse(
                    name = "Not found",
                    responseCode = "404",
                    description = "Aucune organisation ne correspond à cet id",
                    content = @Content(mediaType = "application/json")),
                @APIResponse(
                    name = "Sucessful",
                    responseCode = "200",
                    description = "l'organisation correspondante à cet id",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Organisation.class))) })
        @Operation(
            summary = "getOrganisationById",
            description = "Récupère et renvoie l'organisation correspondante au paramètre")
    @GET @Path("/{langue}/id/{id}") 
    Organisation getOrganisationById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    
    
    @APIResponses(
            value = {
                @APIResponse(
                    name = "Not found",
                    responseCode = "404",
                    description = "Aucune organisation ne correspond à ces critères",
                    content = @Content(mediaType = "application/json")),
                @APIResponse(
                    name = "Sucessful",
                    responseCode = "200",
                    description = "une liste d'organisation correspondantes à ces critères",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Organisation.class))) })
        @Operation(
            summary = "getOrganisationsByCriterias",
            description = "Récupère et renvoie une liste d'organisations correspondantes aux paramètres. La recherche est inclusive. Les paramètres null ou chaine vide ne sont pas pris en compte dans la recherche.")
    @GET @Path("/{langue}/criterias") List<Organisation> getOrganisationsByCriterias(@PathParam("langue") Langue langue, @QueryParam("identifiant") String identifiant, @QueryParam("nom") String nom, @QueryParam("voie") String voie, @QueryParam("ville") String ville, @QueryParam("pays") String pays, @QueryParam("avecInactif") boolean avecInactif) throws BusinessException;

    
    @APIResponses(
            value = {
                @APIResponse(
                    name = "Not found",
                    responseCode = "404",
                    description = "Aucune organisation ne correspond à ces critères",
                    content = @Content(mediaType = "application/json")),
                @APIResponse(
                    name = "Sucessful",
                    responseCode = "200",
                    description = "l'organisation a été désactivée",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Organisation.class))) })
        @Operation(
            summary = "disableOrganisation",
            description = "Récupère et désactive une organisation correspondante au paramètre.")
    @DELETE @Path("/{langue}/disable/{id}") boolean disableOrganisation(@PathParam("langue") Langue langue, @PathParam("id") String idOrganisation) throws BusinessException;
    
    
    @APIResponses(
            value = {
                @APIResponse(
                    name = "Not found",
                    responseCode = "404",
                    description = "Aucune organisation ne peut être synchronisée",
                    content = @Content(mediaType = "application/json")),
                @APIResponse(
                    name = "Sucessful",
                    responseCode = "200",
                    description = "l'organisation mise à jour. Les sous organisations, les droits et les moyens ne sont pas mis à jour.",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Organisation.class))) })
        @Operation(
            summary = "updateOrganisationWithSirene",
            description = "Récupère, complète et renvoie une organisation")
    @POST @Path("/{langue}/sirene") Organisation updateOrganisationWithSirene(@PathParam("langue") Langue langue, Organisation organisation) throws BusinessException;

    
    @APIResponses(
            value = {
                @APIResponse(
                    name = "Not found",
                    responseCode = "404",
                    description = "Aucune organisation ne peut être synchronisée",
                    content = @Content(mediaType = "application/json")),
                @APIResponse(
                    name = "Sucessful",
                    responseCode = "200",
                    description = "l'organisation mise à jour et enregistrée",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Organisation.class))) })
        @Operation(
            summary = "synchroniserOrganisation",
            description = "Enregistre une organisation")
    @POST @Path("/{langue}") Organisation synchroniserOrganisation(@PathParam("langue") Langue langue, Organisation organisation) throws BusinessException;
    
    
}