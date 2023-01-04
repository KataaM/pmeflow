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
package fr.pmeflow.backend.abonnement.model;

import java.util.ArrayList;
import java.util.Date;
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
import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.referentiel.abonnement.model.RefAbonnement;
import fr.pmeflow.referentiel.prestation.model.RefPrestation;
import fr.lixbox.common.util.DateUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import redis.clients.jedis.search.Schema;

/**
 * Cette classe est l'entite representant un abonnement.
 * 
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Abonnement extends AbstractValidatedEntity implements RedisSearchDao
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202111162304L;

	private String oid;
	
	private String libelle;
	private String description;
	private double prixHT;
	private int dureeEngagement;
	private StatutAbonnement etat;
	private Date dateDebut;
	private String motifResiliation;
	
	private String paymentMethodId;
	private String verifPaymentId;
	
	private List<RefPrestation> prestationsIncluses;
	private Client client;
	
	

    // ----------- Methode -----------
	public static Abonnement valueOf(String json)
	{
	    return JsonUtil.transformJsonToObject(json, new TypeReference<Abonnement>() {});
	}
	
	
	
	public Abonnement() 
	{
	    //juste for exsit
	}
	public Abonnement(RefAbonnement refAbonnement)
	{
	    this.setDateDebut(new Date());
	    this.setDescription(refAbonnement.getDescription());
	    this.setDureeEngagement(refAbonnement.getDureeEngagement());
	    this.setEtat(StatutAbonnement.ACTIF);
	    this.setLibelle(refAbonnement.getLibelle());
	    this.setPrestationsIncluses(refAbonnement.getPrestationsIncluses());
	    this.setPrixHT(refAbonnement.getPrixHT());
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

	
	
	@NotEmpty @Size(max=80)
	public String getLibelle()
    {
        return libelle;
    }
    public void setLibelle(String libelle)
    {
        this.libelle = libelle;
    }
	
	
	
	@NotEmpty @Size(max=256)
	public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    

    
    public StatutAbonnement getEtat()
    {
        return etat;
    }
    public void setEtat(StatutAbonnement etat)
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
    
    
    
    
    public String getPaymentMethodId()
    {
        return paymentMethodId;
    }
    public void setPaymentMethodId(String paymentMethodId)
    {
        this.paymentMethodId = paymentMethodId;
    }



    public String getVerifPaymentId()
    {
        return verifPaymentId;
    }
    public void setVerifPaymentId(String verifPaymentId)
    {
        this.verifPaymentId = verifPaymentId;
    }



    @NotEmpty
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

    
    
    @NotNull
    public Date getDateDebut()
    {
        return dateDebut;
    }
    public void setDateDebut(Date dateDebut)
    {
        this.dateDebut = dateDebut;
    }
    
    
    
    @NotNull
    public Client getClient()
    {
        return client;
    }
    public void setClient(Client client)
    {
        this.client = client;
    }
    
    
    
    public int getCreditsDossier()
    {
        int credit = 0;
        
        for (RefPrestation prest : getPrestationsIncluses())
        {
            credit += prest.getCreditsDossier();
        }        
        return credit;
    }
    
    
    
    public int getCreditsRdv()
    {
        int credit = 0;
        
        for (RefPrestation prest : getPrestationsIncluses())
        {
            credit += prest.getCreditsRdv();
        }        
        return credit;
    }
    
        
    
    public String getMotifResiliation()
    {
        return motifResiliation;
    }
    public void setMotifResiliation(String motifResiliation)
    {
        this.motifResiliation = motifResiliation;
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
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+Abonnement.class.getName()+":"+oid;
    }



    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableNumericField("jourPrelevement")
                .addSortableTextField("libelle", 1)
                .addSortableTextField("etat", 1)
                .addSortableNumericField("dateDebut")
                .addSortableTextField("description", 1)
                .addSortableTextField("client_id", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("jourPrelevement", RedisSearchValueSanitizer.sanitizeValue(DateUtil.format(dateDebut, "dd")));
        indexFields.put("libelle", RedisSearchValueSanitizer.sanitizeValue(libelle));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        indexFields.put("etat", RedisSearchValueSanitizer.sanitizeValue(etat));
        indexFields.put("client_id", RedisSearchValueSanitizer.sanitizeValue(getClient().getOid()));
        indexFields.put("dateDebut", RedisSearchValueSanitizer.sanitizeValue(dateDebut.getTime()));
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
        result = prime * result + Objects.hash(client, dateDebut, description, dureeEngagement,
                etat, libelle, motifResiliation, oid, paymentMethodId, prestationsIncluses, prixHT,
                verifPaymentId);
        return result;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Abonnement other = (Abonnement) obj;
        return Objects.equals(client, other.client) && Objects.equals(dateDebut, other.dateDebut)
                && Objects.equals(description, other.description)
                && dureeEngagement == other.dureeEngagement && etat == other.etat
                && Objects.equals(libelle, other.libelle)
                && Objects.equals(motifResiliation, other.motifResiliation)
                && Objects.equals(oid, other.oid)
                && Objects.equals(paymentMethodId, other.paymentMethodId)
                && Objects.equals(prestationsIncluses, other.prestationsIncluses)
                && Double.doubleToLongBits(prixHT) == Double.doubleToLongBits(other.prixHT)
                && Objects.equals(verifPaymentId, other.verifPaymentId);
    }



    public String toPrettyString()
    {
        StringBuilder sbf = new StringBuilder("abonnement ");
        sbf.append(libelle+" ");
        sbf.append("pour ");
        sbf.append(client.getNom()+" "+client.getPrenom());
        return sbf.toString();
    }    
}