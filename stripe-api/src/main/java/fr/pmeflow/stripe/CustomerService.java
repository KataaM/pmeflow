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
package fr.pmeflow.stripe;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.service.common.MicroService;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Client;

/**
 * Cette interface represente les fonctions disponibles
 * dans le service customer de stripe.
 * 
 * @author ludovic.terral
 */
@Path(CustomerService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8", MediaType.APPLICATION_FORM_URLENCODED+"; charset=utf-8",
            MediaType.MULTIPART_FORM_DATA+"; charset=utf-8" })
public interface CustomerService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 202209081359L;
    static final String SERVICE_NAME = "stripe-service-api-customer";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/customer/";
    static final String FULL_SERVICE_URI = "/stripe/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "CUSTSERV";
    
    

    // ----------- Methodes -----------     
    @GET @Path("/{langue}/{customerId}") Client getClientById(@PathParam("langue") Langue langue, @QueryParam("privateKey") String privateKey, @PathParam("customerId") String customerId) throws BusinessException;
    @POST @Path("/{langue}") Client mergeClient(@PathParam("langue") Langue langue, @QueryParam("privateKey") String privateKey, Client client) throws BusinessException;
}
