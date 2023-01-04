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
package fr.pmeflow.commons.authentification;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;


/**
 * Cet authentificateur est destiné à l'authenfification par token.
 * 
 * @author ludovic.terral
 */
public class BearerAuthenticator implements ClientRequestFilter
{
    // ----------- Attribut(s) -----------   
    private final String token;

    
    
    
    // ----------- Methode(s) -----------
    public BearerAuthenticator(String token)
    {
        this.token = token;
    }



    public void filter(ClientRequestContext requestContext) throws IOException
    {
        requestContext.getHeaders().putSingle("Authorization", getBearerAuthentication());
    }



    private String getBearerAuthentication()
    {
        return "Bearer " + token;
    }
}
