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
package fr.pmeflow.referentiel.parametre;

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

import fr.pmeflow.referentiel.parametre.model.Parametre;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les methodes accessibles a distance
 * concernant le service de parametres.
 *
 * @author ludovic.terral
 */
@Path(ParametreService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface ParametreService extends MicroService
{
	// ----------- Attribut -----------
	static final long serialVersionUID = 201705120951L;

    static final String SERVICE_NAME = "pmeflow-referentiel-api-parametre";
    static final String SERVICE_CODE = "PARAMSERV";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/parametre";
    static final String FULL_SERVICE_URI = "/referentiel/api/"+SERVICE_URI;



	// ----------- Methode -----------
    @GET    @Path("/params") List<Parametre> getParametres() throws BusinessException;
    @GET    @Path("/param/{id}") Parametre getParametreById(@PathParam("id") String id) throws BusinessException;
    @POST   @Path("/param/sync") Parametre synchronize(Parametre param) throws BusinessException;
    @DELETE @Path("/param/{id}") boolean remove(@PathParam("id") String id) throws BusinessException;
    
    @GET    @Path("/{serviceId}/params") List<Parametre> getParametresByService(@PathParam("serviceId") String serviceId) throws BusinessException;
    @GET    @Path("/{serviceId}/param/{code}") Parametre getParametreByCode(@PathParam("serviceId") String serviceId, @PathParam("code") String code) throws BusinessException;
    @GET    @Path("/{serviceId}/value/{code}") <T> T getValueByCode(@PathParam("serviceId") String serviceId, @PathParam("code") String code, @QueryParam("default") String defaultValue, @QueryParam("defaultClass") String defaultValueClass) throws BusinessException;
}