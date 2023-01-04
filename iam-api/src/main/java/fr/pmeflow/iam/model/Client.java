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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fr.lixbox.orm.entity.model.ValidatedDao;
import fr.pmeflow.iam.model.enumeration.ModePriseContact;

/**
 * Cette interface est l'entite representant un client.
 * 
 * @author ludovic.terral
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
    @JsonSubTypes({
        @Type(value = Utilisateur.class, name = "Utilisateur") })
public interface Client extends Serializable, ValidatedDao
{
    String getOid();
    void setOid(String oid);
    String getNom();
    void setNom(String nom);
    String getPrenom();
    void setPrenom(String prenom);
    String getEmail();
    void setEmail(String email);
    String getAdresse();
    void setAdresse(String adresse);
    String getCodePostal();
    void setCodePostal(String codePostal);
    String getVille();
    void setVille(String ville);
    String getTelephone();
    void setTelephone(String telephone);
    ModePriseContact getPriseContact();
    void setPriseContact(ModePriseContact priseContact);
    String getCustomerStripeId();
    void setCustomerStripeId(String customerStripeId);
}