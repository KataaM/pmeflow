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
package fr.pmeflow.commons.model.enumeration;

/**
 * Cette classe d'enumeration regroupe tous les etats de base necessaire au
 * cycle de vie d'une entite
 * 
 * @author Ludovic.terral
 */
public enum Etat
{
    // ----------- Attribut -----------
    ACTIF,
    EN_ATTENTE,
    INACTIF,
    ANNULE;
    
    
    private final String libelle;
    private final String libelleCourt;

    
    
    // ----------- Methode -----------
    private Etat()
    {
        this.libelle = name();
        this.libelleCourt = name();
    }

    
    
    public String getLibelle()
    {
        return this.libelle;
    }



    public String getLibelleCourt()
    {
        return this.libelleCourt;
    }
    
    public static Boolean convertToBoolean(Etat toConvert) {
    	if (toConvert == Etat.ACTIF) {
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public static Etat convertToEtat(boolean toConvert)
    {
    	if (toConvert)
    	{
    		return Etat.ACTIF;
    	}
    	else 
    	{
    		return Etat.INACTIF;
    	}
    }


    @Override
    public String toString()
    {
        return this.libelle;
    }
}