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
package fr.pmeflow.referentiel.prestation;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.referentiel.prestation.model.RefPrestation;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Prestation.
 * 
 * @author ludovic.terral
 */
@Path(RefPrestationService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface RefPrestationService extends MicroService
{
	// ----------- Attributs -----------
    static final long serialVersionUID = 202111182303L;    
    static final String SERVICE_NAME = "pmeflow-referentiel-api-prestation";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/prestation";
    static final String FULL_SERVICE_URI = "/referentiel/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "REFPRESTSERV";
    


	// ----------- Methodes -----------
    @GET @Path("/{langue}/all") List<RefPrestation> getPrestations(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") RefPrestation getPrestationById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}") RefPrestation mergePrestation(@PathParam("langue") Langue langue, RefPrestation prestation) throws BusinessException;    
}