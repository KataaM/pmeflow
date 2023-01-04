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
package fr.pmeflow.stripe.helper;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

/**
 * Ce provider ajoute une extension dans le service JAXRS
 * 
 * @author ludovic.terral
 */
@Provider
public class CORSProvider implements Feature 
{
    // ----------- Methode(s) -----------  
    @Override
    public boolean configure(FeatureContext context) 
    {
        CorsFilter filter = new CorsFilter();
        filter.getAllowedOrigins().add("*");
        filter.setAllowedMethods("GET, POST, OPTIONS, HEAD, DELETE");
        filter.setAllowedHeaders("access-control-allow-origin, authorization , accept, content-type, origin");
        context.register(filter);
        return true;
    }    
}