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
package fr.pmeflow.commons;

/**
 * Cette classe regroupe l'ensemble des constantes de l'application Pmeflow
 * 
 * @author Ludovic.terral
 */
public class PmeflowConstant
{
    // ----------- Methode(s) -----------
    private PmeflowConstant()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String PMEFLOW_APP_CLIENT_ID = "5f818065-7387-4024-9bc1-05ac1af9457d";
    public static final String PMEFLOW_CODE = "pmeflow";
    public static final String REDIS_NAME = "global-service-api-redis";
    public static final String REDIS_VERSION = "1.0";
    
    public static final String MSG_MISE_A_JOUR_DOSSIER = "Mise à jour du dossier";
}
