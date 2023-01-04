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
package fr.pmeflow.referentiel.abonnement;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.referentiel.abonnement.model.RefAbonnement;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Abonnement.
 * 
 * @author ludovic.terral
 */
@Path(RefAbonnementService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface RefAbonnementService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 202111131848L;
    static final String SERVICE_NAME = "pmeflow-referentiel-api-abonnement";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/abonnement";
    static final String FULL_SERVICE_URI = "/referentiel/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "REFABOSERV";
    
    

    // ----------- Methodes -----------     
    @GET @Path("/{langue}/all") List<RefAbonnement> getAbonnements(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") RefAbonnement getAbonnementById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}") RefAbonnement mergeAbonnement(@PathParam("langue") Langue langue, RefAbonnement abonnement) throws BusinessException;
}
