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
package fr.pmeflow.referentiel.parametre.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.commons.PmeflowConstant;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.entity.model.Dao;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import redis.clients.jedis.search.Schema;

/**
 * Cette entite correspond aux informations d'un paramètre
 * d'une application.
 *  
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Parametre extends AbstractValidatedEntity implements Dao, RedisSearchDao
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202006131456L;
	
	private String oid;
	private String code;
	private String libelle;
	private String value;
	private String valueClass;
	private String serviceId;



	// ----------- Methode -----------
    public String getOid()
    {
        return oid;
    }
    public void setOid(String oid)
    {
        this.oid = oid;
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
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    
    

    @NotNull @NotEmpty
    public String getValueClass()
    {
        return valueClass;
    }
    public void setValueClass(String valueClass)
    {
        this.valueClass = valueClass;
    }
    
    

    @NotNull @NotEmpty
    public String getServiceId()
    {
        return serviceId;
    }
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    
    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }
    
    
    
    public static Parametre valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<Parametre>() {});
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public String getKey()
    {
        return PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+Parametre.class.getName()+":"+oid;
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema() 
    {
        return new Schema()
                .addSortableTextField("code", 2)
                .addSortableTextField("service", 2);
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("code", RedisSearchValueSanitizer.sanitizeValue(code));
        indexFields.put("service", RedisSearchValueSanitizer.sanitizeValue(serviceId));
        return indexFields;
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
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
        result = prime * result + Objects.hash(code, libelle, oid, serviceId, value, valueClass);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Parametre other = (Parametre) obj;
        return Objects.equals(code, other.code) && Objects.equals(libelle, other.libelle)
                && Objects.equals(oid, other.oid) && Objects.equals(serviceId, other.serviceId)
                && Objects.equals(value, other.value)
                && Objects.equals(valueClass, other.valueClass);
    }
}