/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.rdv.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.model.enumeration.Etat;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.iam.model.Utilisateur;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import redis.clients.jedis.search.Schema;

/**
 * Cette entité représente un rendez-vous.
 *
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class RendezVous extends AbstractValidatedEntity  implements RedisSearchDao
{
    // ----------- Attributs -----------
    private static final long serialVersionUID = 202204232335L;

    private String oid;
    
    private String motif;
    private String description;
    private Etat etat;
    private String dossierId;
    private Date dateDebut;
    private Date dateFin;
    
    private Client client;
    private Utilisateur juriste;
    
    

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
    


    @NotEmpty
    public String getMotif()
    {
        return motif;
    }
    public void setMotif(String motif)
    {
        this.motif = motif;
    }
    
    

    @Size(max=256)
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
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
    
    

    @NotNull
    public Utilisateur getJuriste()
    {
        return juriste;
    }
    public void setJuriste(Utilisateur juriste)
    {
        this.juriste = juriste;
    }
    
    

    @NotNull
    public Etat getEtat()
    {
        if (this.etat==null)
        {
            this.etat=Etat.ACTIF;
        }
        return etat;
    }
    public void setEtat(Etat etat)
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
    public Date getDateFin()
    {
        return dateFin;
    }
    public void setDateFin(Date dateFin)
    {
        this.dateFin = dateFin;
    }


    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }



    @Override
    public String getKey()
    {
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+RendezVous.class.getCanonicalName()+":"+oid;
    }



    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableTextField("client_id", 1)
                .addSortableTextField("juriste_id", 1)
                .addSortableTextField("dossier_id", 1)
                .addSortableTextField("motif", 1)
                .addSortableTextField("description", 1)
                .addSortableNumericField("dateDebut")
                .addSortableNumericField("dateFin")
                .addSortableTextField("etat", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("client_id", RedisSearchValueSanitizer.sanitizeValue(client!=null?client.getOid():""));
        indexFields.put("juriste_id", RedisSearchValueSanitizer.sanitizeValue(juriste!=null?juriste.getOid():""));
        indexFields.put("dossier_id", RedisSearchValueSanitizer.sanitizeValue(dossierId));
        indexFields.put("motif", RedisSearchValueSanitizer.sanitizeValue(motif));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        indexFields.put("etat", RedisSearchValueSanitizer.sanitizeValue(etat));
        indexFields.put("dateDebut", RedisSearchValueSanitizer.sanitizeValue(dateDebut.getTime()));
        indexFields.put("dateFin", RedisSearchValueSanitizer.sanitizeValue(dateFin.getTime()));
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
        result = prime * result
                + Objects.hash(client, dateDebut, description, dossierId, etat, juriste, motif, oid);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        RendezVous other = (RendezVous) obj;
        return Objects.equals(client, other.client) && Objects.equals(dateDebut, other.dateDebut)
                && Objects.equals(description, other.description)
                && Objects.equals(dossierId, other.dossierId) && etat == other.etat
                && Objects.equals(juriste, other.juriste) && Objects.equals(motif, other.motif)
                && Objects.equals(oid, other.oid);
    }
}