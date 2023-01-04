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

import java.util.Map;

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
 * dans le service Payment.
 * 
 * @author ludovic.terral
 */
@Path(PaymentService.SERVICE_URI)
@Produces({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
@Consumes({ MediaType.APPLICATION_JSON+"; charset=utf-8" })
public interface PaymentService  extends MicroService
{  
    // ----------- Attribut(s) -----------   
    static final long serialVersionUID = 202208121146L;
    static final String SERVICE_NAME = "stripe-service-api-payment";
    static final String SERVICE_VERSION = "1.0";
    static final String SERVICE_URI = SERVICE_VERSION+"/payment/";
    static final String FULL_SERVICE_URI = "/stripe/api/"+SERVICE_URI;
    static final String SERVICE_CODE = "PAYSERV";
    
    

    // ----------- Methodes -----------     
    @GET @Path("/{langue}/prepare") Map<String, String> preparePayment(@PathParam("langue") Langue langue, @QueryParam("privateKey") String privateKey, @QueryParam("amount") double amount, @QueryParam("description") String description, @QueryParam("customerId") String customerId, @QueryParam("savePaymentMethod") boolean savePaymentMethod) throws BusinessException;
    @GET @Path("/{langue}/pay") Map<String, String> pay(@PathParam("langue") Langue langue, @QueryParam("privateKey") String privateKey, @QueryParam("amount") double amount, 
            @QueryParam("description") String description, @QueryParam("customerId") String customerId, @QueryParam("paymentMethodId") String paymentMethodId) throws BusinessException;
    
    @GET @Path("/{langue}/paymentMethod/card") String createCardPaymentMethod(@PathParam("langue") Langue langue, @QueryParam("privateKey") String privateKey, @QueryParam("number") String number, @QueryParam("month") String month,
            @QueryParam("year") String year, @QueryParam("cvc") String cvc) throws BusinessException;
    @POST @Path("/{langue}/paymentMethod/sepa") String createSepaPaymentMethod(@PathParam("langue") Langue langue, 
            @QueryParam("privateKey") String privateKey, Client client, @QueryParam("iban") String iban) throws BusinessException;
}