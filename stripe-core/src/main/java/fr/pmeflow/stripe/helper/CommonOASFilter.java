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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;

import fr.lixbox.common.util.StringUtil;

/**
 * Ce filtre supprime les données de l'api qui ne doivent pas être diffusées
 * 
 * @author ludovic.terral
 */
public class CommonOASFilter implements OASFilter
{
    // ----------- Methode(s) -----------
    @Override
    public void filterOpenAPI(OpenAPI openAPI) 
    {
        //selectionner que les services contenus dans le projet
        Map<String, PathItem> pathItems = new HashMap<>(openAPI.getPaths().getPathItems());
        
        for (Entry<String, PathItem> entry : pathItems.entrySet())
        {
            if (!(StringUtil.contains(entry.getKey(), "/1.0/customer")||
                  StringUtil.contains(entry.getKey(), "/1.0/payment")))
            {
                openAPI.getPaths().removePathItem(entry.getKey());
            }
        }
    }
}
