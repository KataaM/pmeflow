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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Profil;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Profil.
 * 
 * @author ludovic.terral
 */
@Path(ProfilService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface ProfilService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 202111131848L;
    static final String SERVICE_NAME = "pmeflow-service-api-profil";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/profil/";
    static final String FULL_SERVICE_URI = "/iam/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "PROFSERV";
    
    

    // ----------- Methodes -----------     
    @GET @Path("/{langue}") Profil getProfil(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/disponibilite") Map<String, Map<String, Map<String, Integer>>> getDisponibilite(@PathParam("langue") Langue langue) throws BusinessException;
    @POST @Path("/{langue}") Profil mergeProfil(@PathParam("langue") Langue langue, Profil profil) throws BusinessException;
    @POST @Path("/{langue}/password") Boolean changePassword(@PathParam("langue") Langue langue, String password) throws BusinessException;    
    @GET @Path("/{langue}/reinit") Boolean reinitialiserCompte(@PathParam("langue") Langue langue, @QueryParam("email") String email) throws BusinessException;
    
}
