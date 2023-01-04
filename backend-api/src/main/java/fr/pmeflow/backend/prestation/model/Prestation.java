/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.prestation.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.referentiel.prestation.model.RefPrestation;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import redis.clients.jedis.search.Schema;

/**
 * Cette classe représente une prestation
 * 
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Prestation extends AbstractValidatedEntity  implements RedisSearchDao
{    
    // ----------- Attributs -----------
    private static final long serialVersionUID = 202111182249L;
    
    private String oid;
    private Date dateAchat;
    private String libelle;
    private String description;
    private double prixHT;
    private int creditsRdv=0;
    private int creditsDossier=0;
    private StatutPrestation etat;
    private String dossierId;
    private String paymentIntentId;
    private String abonnementId;
    
    private Client client;
    
    

    // ----------- Methodes -----------   
    public static Prestation valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<Prestation>() {});
    }
    
    
    
    public Prestation() 
    {
        //juste for exsit
    }
    public Prestation(RefPrestation refPrestation)
    {
        this.setPrixHT(refPrestation.getPrixHT());
        this.setCreditsDossier(refPrestation.getCreditsDossier());
        this.setCreditsRdv(refPrestation.getCreditsRdv());
        this.setDescription(refPrestation.getDescription());
        this.setLibelle(refPrestation.getLibelle());
        this.setEtat(StatutPrestation.A_PAYER);
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
    
    
    
    @NotNull
    public Date getDateAchat()
    {
        return dateAchat;
    }
    public void setDateAchat(Date dateAchat)
    {
        this.dateAchat = dateAchat;
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
    public void setPrixHT(double prix)
    {
        this.prixHT = prix;
    }
    
    

    @NotNull
    public StatutPrestation getEtat()
    {
        return etat;
    }
    public void setEtat(StatutPrestation etat)
    {
        this.etat = etat;
    }
    
    

    public String getDossierId()
    {
        return dossierId;
    }
    public void setDossierId(String dossierId)
    {
        this.dossierId = dossierId;
    }


    
    public String getAbonnementId()
    {
        return abonnementId;
    }
    public void setAbonnementId(String abonnementId)
    {
        this.abonnementId = abonnementId;
    }



    public String getPaymentIntentId()
    {
        return paymentIntentId;
    }
    public void setPaymentIntentId(String paymentIntentId)
    {
        this.paymentIntentId = paymentIntentId;
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
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+Prestation.class.getName()+":"+oid;
    }



    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addNumericField("dateAchat")
                .addSortableTextField("libelle", 1)
                .addSortableTextField("etat", 1)
                .addSortableTextField("description", 1)
                .addSortableTextField("client_id", 1)
                .addSortableTextField("dossier_id", 1)
                .addSortableTextField("abonnement_id", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("dateAchat", RedisSearchValueSanitizer.sanitizeValue(dateAchat.getTime()));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        indexFields.put("libelle", RedisSearchValueSanitizer.sanitizeValue(libelle));
        indexFields.put("etat", RedisSearchValueSanitizer.sanitizeValue(etat));
        indexFields.put("client_id", RedisSearchValueSanitizer.sanitizeValue(getClient().getOid()));
        indexFields.put("dossier_id", RedisSearchValueSanitizer.sanitizeValue(dossierId));
        indexFields.put("abonnement_id", RedisSearchValueSanitizer.sanitizeValue(abonnementId));
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
        result = prime * result + Objects.hash(abonnementId, client, creditsDossier, creditsRdv,
                dateAchat, description, dossierId, etat, libelle, oid, paymentIntentId, prixHT);
        return result;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Prestation other = (Prestation) obj;
        return Objects.equals(abonnementId, other.abonnementId)
                && Objects.equals(client, other.client) && creditsDossier == other.creditsDossier
                && creditsRdv == other.creditsRdv && Objects.equals(dateAchat, other.dateAchat)
                && Objects.equals(description, other.description)
                && Objects.equals(dossierId, other.dossierId) && etat == other.etat
                && Objects.equals(libelle, other.libelle) && Objects.equals(oid, other.oid)
                && Objects.equals(paymentIntentId, other.paymentIntentId)
                && Double.doubleToLongBits(prixHT) == Double.doubleToLongBits(other.prixHT);
    }



    public String toPrettyString()
    {
        StringBuilder sbf = new StringBuilder("prestation ");
        sbf.append(libelle+" ");
        sbf.append("pour ");
        sbf.append(client.getNom()+" "+client.getPrenom());
        return sbf.toString();
        
    }  
}