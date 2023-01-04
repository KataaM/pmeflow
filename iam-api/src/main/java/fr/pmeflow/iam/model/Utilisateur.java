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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.iam.model.enumeration.ModePriseContact;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;

/**
 * Cette classe est l'entite representant un utilisateur.
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Utilisateur extends AbstractValidatedEntity implements Client, Profil
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 4307563073452678647L;

	private String oid;
	
	private String nom;
	private String prenom;
	private String adresse;
	private String codePostal;
	private String ville;
    private String specialite;
    private Map<String, Map<String, Map<String, Integer>>> disponibilite;

    private String login;
    private String password;
	private String email;
	private String telephone;
	private ModePriseContact priseContact;
	private List<ProfilPmeflow> profils;

    private String customerStripeId;
	

    // ----------- Methode -----------
    public static Utilisateur valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<Utilisateur>() {});
    }
    
    
    
    @Override	 
	public String getOid()
	{
		return oid;
	}
	@Override
	public void setOid(String oid)
	{
		this.oid = oid;
	}

	
	
	@NotNull @NotEmpty @Size(max=125)
	public String getNom()
	{
		return nom;
	}
	public void setNom(String nom)
	{
	    if (nom!=null)
	    {
	        nom = nom.toUpperCase();
	    }
		this.nom = nom;
	}
	
	
	
	@NotNull @NotEmpty @Size(max=125)
	public String getPrenom()
	{
		return prenom;
	}
	public void setPrenom(String prenom)
	{
        if (prenom!=null)
        {
            prenom = StringUtils.capitalize(prenom.toLowerCase());
        }
		this.prenom = prenom;
	}
	
	
	
	@NotNull @NotEmpty @Size(max=125)
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	

	@Size(max=256)
    public String getAdresse()
    {
        return adresse;
    }
    public void setAdresse(String adresse)
    {
        this.adresse = adresse;
    }


    
    @Size(max=5)
    public String getCodePostal()
    {
        return codePostal;
    }
    public void setCodePostal(String codePostal)
    {
        this.codePostal = codePostal;
    }



    @Size(max=80)
    public String getVille()
    {
        return ville;
    }
    public void setVille(String ville)
    {
        this.ville = ville;
    }



    @Size(max=255)
    public String getSpecialite()
    {
        return specialite;
    }
    public void setSpecialite(String specialite)
    {
        this.specialite = specialite;
    }
    
    
    
    @Size(max=20)
    public String getTelephone()
    {
        return telephone;
    }
    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }



    @NotNull
    public ModePriseContact getPriseContact()
    {
        return priseContact;
    }
    public void setPriseContact(ModePriseContact priseContact)
    {
        this.priseContact = priseContact;
    }
    


    public List<ProfilPmeflow> getProfils()
    {
        if (profils==null)
        {
            profils = new ArrayList<>();
        }
        return profils;
    }
    public void setProfils(List<ProfilPmeflow> profils)
    {
        this.profils = profils;
    }
    
    
    
    public Map<String, Map<String, Map<String, Integer>>> getDisponibilite()
    {
        if (disponibilite==null)
        {
            disponibilite = new LinkedHashMap<>(); 
        }
        return disponibilite;
    }
    public void setDisponibilite(Map<String, Map<String, Map<String, Integer>>> disponibilite)
    {
        this.disponibilite = disponibilite;
    }

    

    public String getLogin()
    {
        return login;
    }
    public void setLogin(String login)
    {
        this.login = login;
    }



    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }



    public String getCustomerStripeId()
    {
        return customerStripeId;
    }
    public void setCustomerStripeId(String customerStripeId)
    {
        this.customerStripeId = customerStripeId;
    }



    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(adresse, codePostal, customerStripeId, disponibilite,
                email, login, nom, oid, password, prenom, priseContact, profils, specialite,
                telephone, ville);
        return result;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Utilisateur other = (Utilisateur) obj;
        return Objects.equals(adresse, other.adresse)
                && Objects.equals(codePostal, other.codePostal)
                && Objects.equals(customerStripeId, other.customerStripeId)
                && Objects.equals(disponibilite, other.disponibilite)
                && Objects.equals(email, other.email) && Objects.equals(login, other.login)
                && Objects.equals(nom, other.nom) && Objects.equals(oid, other.oid)
                && Objects.equals(password, other.password) && Objects.equals(prenom, other.prenom)
                && priseContact == other.priseContact && Objects.equals(profils, other.profils)
                && Objects.equals(specialite, other.specialite)
                && Objects.equals(telephone, other.telephone) && Objects.equals(ville, other.ville);
    }
}