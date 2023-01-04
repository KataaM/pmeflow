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
package fr.pmeflow.iam.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Cette interface est l'entite representant un profil utilisateur.
 * 
 * @author ludovic.terral
 */
public interface Profil extends Serializable
{
    String getLogin();
    void setLogin(String login);
    String getPassword();
    void setPassword(String password);
    String getSpecialite();
    void setSpecialite(String specialite);
    Map<String, Map<String, Map<String, Integer>>> getDisponibilite();
    void setDisponibilite(Map<String, Map<String, Map<String, Integer>>> disponibilite);
}