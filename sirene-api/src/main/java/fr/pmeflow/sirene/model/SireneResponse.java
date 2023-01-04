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
package fr.pmeflow.sirene.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.sirene.model.etablissement.Etablissement;
import fr.pmeflow.sirene.model.uniteLegale.UniteLegale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SireneResponse
{
    // ----------- Attribut(s) -----------   
    Header header;
    UniteLegale uniteLegale;
    Etablissement etablissement;
    

    
    // ----------- Methode(s) -----------
    public Header getHeader()
    {
        return header;
    }
    public void setHeader(Header header)
    {
        this.header = header;
    }
    
    
    
    public UniteLegale getUniteLegale()
    {
        return uniteLegale;
    }
    public void setUniteLegale(UniteLegale uniteLegale)
    {
        this.uniteLegale = uniteLegale;
    }
    
    
    
    public Etablissement getEtablissement()
    {
        return etablissement;
    }
    public void setEtablissement(Etablissement etablissement)
    {
        this.etablissement = etablissement;
    }
}
