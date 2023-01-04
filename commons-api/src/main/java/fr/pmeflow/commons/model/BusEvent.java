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
package fr.pmeflow.commons.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.io.json.JsonUtil;

/**
 * Ce pojo correspond au contenu d'un évènement du bus MOM.
 * 
 * @author ludovic.terral
 */
public class BusEvent implements Serializable
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202205051021L;

    private String name;
    private transient Map<String, String> properties;
    private String body;
    
    

    // ----------- Methode(s) -----------
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    
    public Map<String, String> getProperties()
    {
        if (properties==null)
        {
            properties=new HashMap<>();
        }
        return properties;
    }
    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }
    
    
    
    public String getBody()
    {
        return body;
    }
    public void setBody(String body)
    {
        this.body = body;
    }



    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }



    public static BusEvent fromString(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<BusEvent>(){});
    }
}