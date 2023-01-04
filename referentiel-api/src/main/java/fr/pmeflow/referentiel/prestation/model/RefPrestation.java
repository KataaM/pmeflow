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
package fr.pmeflow.referentiel.prestation.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.commons.PmeflowConstant;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import redis.clients.jedis.search.Schema;

/**
 * Cette classe représente une prestation
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RefPrestation extends AbstractValidatedEntity  implements RedisSearchDao
{    
    // ----------- Attributs -----------
    private static final long serialVersionUID = 202111182249L;
    
    private String oid;
    private String code;
    private String libelle;
    private String description;
    private double prixHT;
    private int creditsRdv=0;
    private int creditsDossier=0;
    private boolean etat;    
    private String stripeProductId;
    
    

    // ----------- Methodes -----------    
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
    

    
    public int getCreditsRdv()
    {
        return creditsRdv;
    }
    public void setCreditsRdv(int creditsRdv)
    {
        this.creditsRdv = creditsRdv;
    }
    
    
    
    public int getCreditsDossier()
    {
        return creditsDossier;
    }
    public void setCreditsDossier(int creditsDossier)
    {
        this.creditsDossier = creditsDossier;
    }
    
    
    
    @NotNull @NotEmpty
    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }
    
    
    
    @NotNull @NotEmpty
    public String getLibelle()
    {
        return libelle;
    }
    public void setLibelle(String libelle)
    {
        this.libelle = libelle;
    }
    
    
    
    @NotNull @NotEmpty
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    
    
    public double getPrixHT()
    {
        return prixHT;
    }
    public void setPrix(double prixHT)
    {
        this.prixHT = prixHT;
    }
    
    
    
    public boolean isEtat()
    {
        return etat;
    }
    public void setEtat(boolean etat)
    {
        this.etat = etat;
    }
    
        
    
    public String getStripeProductId()
    {
        return stripeProductId;
    }
    public void setStripeProductId(String stripeProductId)
    {
        this.stripeProductId = stripeProductId;
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
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+RefPrestation.class.getName()+":"+oid;
    }



    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableTextField("code", 1)
                .addSortableTextField("description", 1)
                .addSortableTextField("libelle", 1)
                .addSortableTextField("stripeProductId", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("code", RedisSearchValueSanitizer.sanitizeValue(code));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        indexFields.put("libelle", RedisSearchValueSanitizer.sanitizeValue(libelle));
        indexFields.put("stripeProductId", RedisSearchValueSanitizer.sanitizeValue(stripeProductId));
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
        result = prime * result + Objects.hash(code, creditsDossier, creditsRdv, description, etat,
                libelle, oid, prixHT, stripeProductId);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        RefPrestation other = (RefPrestation) obj;
        return Objects.equals(code, other.code) && creditsDossier == other.creditsDossier
                && creditsRdv == other.creditsRdv && Objects.equals(description, other.description)
                && etat == other.etat && Objects.equals(libelle, other.libelle)
                && Objects.equals(oid, other.oid)
                && Double.doubleToLongBits(prixHT) == Double.doubleToLongBits(other.prixHT)
                && Objects.equals(stripeProductId, other.stripeProductId);
    }
}