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
package fr.pmeflow.sirene;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.lixbox.common.exceptions.BusinessException;
import fr.pmeflow.sirene.model.SireneResponse;

/**
 * Cette interface reprensente le contrat d'échange avec l'annuaire
 * sirene.
 * 
 * @author Ludovic.terral
 */
@Path(AnnuaireSireneService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8", MediaType.APPLICATION_FORM_URLENCODED+"; charset=utf-8",
            MediaType.MULTIPART_FORM_DATA+"; charset=utf-8" })
public interface AnnuaireSireneService
{
 // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 202209071007L;
    static final String SERVICE_NAME = "global-service-api-sirene";
    static final String SERVICE_VERSION = "V3";
    static final String SERVICE_URI = "/sirene/"+SERVICE_VERSION;
    static final String FULL_SERVICE_URI = SERVICE_URI;
    static final String SERVICE_CODE = "SIRENSERV";
    
    

    // ----------- Methodes -----------     
    @GET @Path("/siren/{siren}") SireneResponse getOrganisationBySiren(@PathParam("siren") String siren) throws BusinessException;
    @GET @Path("/siret/{siret}") SireneResponse getOrganisationBySiret(@PathParam("siret") String siret) throws BusinessException;
}