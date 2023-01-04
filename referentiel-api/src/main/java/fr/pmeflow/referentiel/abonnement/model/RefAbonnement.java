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
package fr.pmeflow.referentiel.abonnement.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.referentiel.prestation.model.RefPrestation;
import redis.clients.jedis.search.Schema;

/**
 * Cette classe est l'entite representant un RefAbonnement.
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RefAbonnement extends AbstractValidatedEntity implements RedisSearchDao
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202111162304L;

	private String oid;
	
	private String libelle;
	private String description;
	private double prixHT;
	private int dureeEngagement;
	private String stripeProductId;
	private boolean etat;
	
	private List<RefPrestation> prestationsIncluses;

	

    // ----------- Methode -----------
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

	
	
	@NotNull @NotEmpty @Size(max=80)
	public String getLibelle()
    {
        return libelle;
    }
    public void setLibelle(String libelle)
    {
        this.libelle = libelle;
    }
	
	
	
	@NotNull @NotEmpty @Size(max=256)
	public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    

    
    public boolean getEtat()
    {
        return etat;
    }
    public void setEtat(boolean etat)
    {
        this.etat = etat;
    }

    

    public double getPrixHT()
    {
        return prixHT;
    }
    public void setPrixHT(double prix)
    {
        this.prixHT = prix;
    }
    
    
    
    public int getDureeEngagement()
    {
        return dureeEngagement;
    }
    public void setDureeEngagement(int dureeEngagement)
    {
        this.dureeEngagement = dureeEngagement;
    }
    
    
    
    public String getStripeProductId()
    {
        return stripeProductId;
    }
    public void setStripeProductId(String stripeProductId)
    {
        this.stripeProductId = stripeProductId;
    }    
    
    
    
    public List<RefPrestation> getPrestationsIncluses()
    {
        if (prestationsIncluses == null)
        {
            this.prestationsIncluses = new ArrayList<>();
        }
        return prestationsIncluses;
    }
    public void setPrestationsIncluses(List<RefPrestation> prestationsIncluses)
    {
        this.prestationsIncluses = prestationsIncluses;
    }
    
    
    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }



    @JsonIgnore
    @XmlTransient
    @Override
    public String getKey()
    {
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+RefAbonnement.class.getName()+":"+oid;
    }



    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableTextField("libelle", 1)
                .addSortableTextField("description", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("libelle", RedisSearchValueSanitizer.sanitizeValue(libelle));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        return indexFields;
    }



    @Override
    public long getTTL()
    {
        return 0;
    }
    
    
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(description, dureeEngagement, etat,
                libelle, oid, prestationsIncluses, prixHT, stripeProductId);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        RefAbonnement other = (RefAbonnement) obj;
        return Objects.equals(description, other.description)
                && dureeEngagement == other.dureeEngagement && etat == other.etat
                && Objects.equals(libelle, other.libelle) && Objects.equals(oid, other.oid)
                && Objects.equals(prestationsIncluses, other.prestationsIncluses)
                && Double.doubleToLongBits(prixHT) == Double.doubleToLongBits(other.prixHT)
                && Objects.equals(stripeProductId, other.stripeProductId);
    }
}