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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Utilisateur;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service Utilisateur.
 * 
 * @author ludovic.terral
 */
@Path(UtilisateurService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8", MediaType.APPLICATION_FORM_URLENCODED+"; charset=utf-8",
            MediaType.MULTIPART_FORM_DATA+"; charset=utf-8" })
public interface UtilisateurService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 202111131848L;
    static final String SERVICE_NAME = "pmeflow-service-api-utilisateur";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/utilisateur/";
    static final String FULL_SERVICE_URI = "/iam/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "UTILSERV";
    
    

    // ----------- Methodes -----------     
    @GET @Path("/{langue}/all") List<Utilisateur> getUtilisateurs(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/juristes") List<Utilisateur> getJuristes(@PathParam("langue") Langue langue) throws BusinessException;
    @GET @Path("/{langue}/{id}") Utilisateur getUtilisateurById(@PathParam("langue") Langue langue, @PathParam("id") String id) throws BusinessException;
    @POST @Path("/{langue}") Utilisateur mergeUtilisateur(@PathParam("langue") Langue langue, Utilisateur client) throws BusinessException;
}
