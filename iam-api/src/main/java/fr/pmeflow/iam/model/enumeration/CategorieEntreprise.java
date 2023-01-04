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
package fr.pmeflow.iam.model.enumeration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cette enum catégorise les entreprises.
 * 
 * @author ludovic.terral
 */
public enum CategorieEntreprise 
{
    // ----------- Attribut -----------
    PME("PME : petite ou moyenne entreprise"),
    ETI("ETI : entreprise de taille intermediaire"),
    GE("GE : grande entreprise"); 

  
    private final String libelle;

    
    
    // ----------- Methode -----------
    private CategorieEntreprise(String libelle)
    {
        this.libelle = libelle;
    }

    
    
    public String getLibelle()
    {
        return this.libelle;
    }



    @Override
    public String toString()
    {
        return this.libelle;
    }
    
    
    
    public static List<Map<String, String>> jsonValues() 
    {
        List<Map<String, String>> jsonResults = new ArrayList<>();
        for (CategorieEntreprise cat : CategorieEntreprise.values())
        {
            Map<String,String> map = new HashMap<>();
            map.put("code", cat.name());
            map.put("libelle", cat.getLibelle());
            jsonResults.add(map);
        }     
        return jsonResults;
    }
}