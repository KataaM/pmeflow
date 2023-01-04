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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.iam.model.Utilisateur;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import redis.clients.jedis.search.Schema;

/**
 * Cette entité représente un evenement.
 *
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Evenement extends AbstractValidatedEntity  implements RedisSearchDao
{
    // ----------- Attributs -----------
    private static final long serialVersionUID = 202205101110L;

    private String oid;
    
    private String libelle;
    private String description;
    private String type;
    private String lieu;
    private Date dateDebut;
    private Date dateFin;
    private boolean jourEntier;
    private Utilisateur utilisateur;
    
    

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
    public String getLibelle()
    {
        return libelle;
    }
    public void setLibelle(String libelle)
    {
        this.libelle = libelle;
    }
    
    
    
    
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    

    @NotEmpty
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
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
    
    

    public String getLieu()
    {
        return lieu;
    }
    public void setLieu(String lieu)
    {
        this.lieu = lieu;
    }
    
    
    
    public boolean isJourEntier()
    {
        return jourEntier;
    }
    public void setJourEntier(boolean jourEntier)
    {
        this.jourEntier = jourEntier;
    }
    
    
    
    @NotNull
    public Utilisateur getUtilisateur()
    {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur)
    {
        this.utilisateur = utilisateur;
    }
    
    
    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }



    @Override
    public String getKey()
    {
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+Evenement.class.getCanonicalName()+":"+oid;
    }



    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableTextField("utilisateur_id", 1)
                .addSortableTextField("libelle", 1)
                .addSortableNumericField("date_debut")
                .addSortableNumericField("date_fin");
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("utilisateur_id", RedisSearchValueSanitizer.sanitizeValue(utilisateur!=null?utilisateur.getOid():""));
        indexFields.put("libelle", RedisSearchValueSanitizer.sanitizeValue(libelle));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        indexFields.put("date_debut", RedisSearchValueSanitizer.sanitizeValue(dateDebut.getTime()));
        indexFields.put("date_fin", RedisSearchValueSanitizer.sanitizeValue(dateFin.getTime()));
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
                + Objects.hash(dateDebut, dateFin, description, libelle, oid, type, utilisateur);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Evenement other = (Evenement) obj;
        return Objects.equals(dateDebut, other.dateDebut) && Objects.equals(dateFin, other.dateFin)
                && Objects.equals(description, other.description)
                && Objects.equals(libelle, other.libelle) && Objects.equals(oid, other.oid)
                && Objects.equals(type, other.type)
                && Objects.equals(utilisateur, other.utilisateur);
    }    
}