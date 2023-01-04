/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.facturation.model;

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

import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.iam.model.Client;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import redis.clients.jedis.search.Schema;

/**
 * Cette classe représente une facture
 * 
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Facture extends AbstractValidatedEntity  implements RedisSearchDao
{    
    // ----------- Attributs -----------
    private static final long serialVersionUID = 202111182249L;
    
    private String oid;
    private Date date;
    private String numero;
    
    private Client client;
    private Prestation prestation;
    
    private double tva;
    private double totalTTC;
    
    

    // ----------- Methodes -----------   
    public static Facture valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<Facture>() {});
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
    

    
    @NotNull
    public Date getDate()
    {
        return date;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }



    @NotEmpty
    public String getNumero()
    {
        return numero;
    }
    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    

    @NotNull
    public Prestation getPrestation()
    {
        return prestation;
    }
    public void setPrestation(Prestation prestation)
    {
        this.prestation = prestation;
    }


    
    public double getTva()
    {
        return tva;
    }
    public void setTva(double tva)
    {
        this.tva = tva;
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
    
       
    
    public double getTotalTTC()
    {
        return totalTTC;
    }
    public void setTotalTTC(double totalTTC)
    {
        this.totalTTC = totalTTC;
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
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+Facture.class.getName()+":"+oid;
    }



    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableNumericField("date")
                .addSortableTextField("client_id", 1)
                .addSortableTextField("prestation_id", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("date", RedisSearchValueSanitizer.sanitizeValue(date.getTime()));
        indexFields.put("client_id", RedisSearchValueSanitizer.sanitizeValue(getClient().getOid()));
        indexFields.put("prestation_id", RedisSearchValueSanitizer.sanitizeValue(getPrestation().getOid()));
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
        result = prime * result + Objects.hash(client, date, numero, oid, prestation, tva);
        return result;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Facture other = (Facture) obj;
        return Objects.equals(client, other.client) && Objects.equals(date, other.date)
                && Objects.equals(numero, other.numero) && Objects.equals(oid, other.oid)
                && Objects.equals(prestation, other.prestation)
                && Double.doubleToLongBits(tva) == Double.doubleToLongBits(other.tva);
    }
}