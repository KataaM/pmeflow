/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.dossier.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.backend.dossier.model.enumeration.EtatDossier;
import fr.pmeflow.backend.io.model.Document;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.iam.model.Utilisateur;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import redis.clients.jedis.search.Schema;

/**
 * Cette classe est l'entite representant un dossier.
 * 
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Dossier extends AbstractValidatedEntity implements RedisSearchDao
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 4307563073452678647L;

	private String oid;
	
	private Calendar dateCreation;
	private String libelle;
	private String description;
    private EtatDossier etat;
	
	private Client client;
	private Utilisateur juriste;
	
	private List<Suivi> suivis;
    private List<Echange> echanges;
    private List<Document> documents;
    private List<Prestation> prestations;
	
	

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
    	
    

    @NotNull
	public Calendar getDateCreation()
    {
        return dateCreation;
    }
    public void setDateCreation(Calendar dateCreation)
    {
        this.dateCreation = dateCreation;
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
    
    

    @NotEmpty
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    
    
    public List<Document> getDocuments()
    {
        if (CollectionUtil.isEmpty(documents)) 
        {
            this.documents = new ArrayList<>();
        }
        return documents;
    }
    public void setDocuments(List<Document> documents)
    {
        this.documents = documents;
    }
    
    
    
    public Client getClient()
    {
        return client;
    }
    public void setClient(Client client)
    {
        this.client = client;
    }
    
    
    
    public Utilisateur getJuriste()
    {
        return juriste;
    }
    public void setJuriste(Utilisateur juriste)
    {
        this.juriste = juriste;
    }
    
    
    
    public List<Echange> getEchanges()
    {
        if (CollectionUtil.isEmpty(echanges)) 
        {
            this.echanges = new ArrayList<>();
        }
        return echanges;
    }
    public void setEchanges(List<Echange> echanges)
    {
        this.echanges = echanges;
    }
    
    
    
    public List<Prestation> getPrestations()
    {
        if (CollectionUtil.isEmpty(prestations)) 
        {
            this.prestations = new ArrayList<>();
        }
        return prestations;
    }
    public void setPrestations(List<Prestation> prestations)
    {
        this.prestations = prestations;
    }
    
    
    
    public EtatDossier getEtat()
    {
        if (this.etat==null)
        {
            this.etat=EtatDossier.NOUVEAU;
        }
        return etat;
    }
    public void setEtat(EtatDossier etat)
    {
        this.etat = etat;
    }
    
    
    
    public List<Suivi> getSuivis()
    {
        if (CollectionUtil.isEmpty(suivis)) 
        {
            this.suivis = new ArrayList<>();
        }
        return suivis;
    }
    public void setSuivis(List<Suivi> suivis)
    {
        this.suivis = suivis;
    }



    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }



    @Override
    public String getKey()
    {
        return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:"+Dossier.class.getCanonicalName()+":"+oid;
    }



    @Override
    public Schema getIndexSchema()
    {
        return new Schema()
                .addSortableTextField("clientId", 1)
                .addSortableTextField("juristeId", 1)
                .addSortableTextField("libelle", 1)
                .addSortableTextField("description", 1)
                .addSortableTextField("etat", 1);
    }



    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("clientId", RedisSearchValueSanitizer.sanitizeValue(client!=null?client.getOid():""));
        indexFields.put("juristeId", RedisSearchValueSanitizer.sanitizeValue(juriste!=null?juriste.getOid():""));
        indexFields.put("libelle", RedisSearchValueSanitizer.sanitizeValue(libelle));
        indexFields.put("description", RedisSearchValueSanitizer.sanitizeValue(description));
        indexFields.put("etat", RedisSearchValueSanitizer.sanitizeValue(etat));
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
        result = prime * result + Objects.hash(client, description, documents, echanges, etat,
                juriste, libelle, oid, suivis);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Dossier other = (Dossier) obj;
        return Objects.equals(client, other.client)
                && Objects.equals(description, other.description)
                && Objects.equals(documents, other.documents)
                && Objects.equals(echanges, other.echanges) && etat == other.etat
                && Objects.equals(juriste, other.juriste) && Objects.equals(libelle, other.libelle)
                && Objects.equals(oid, other.oid) && Objects.equals(suivis, other.suivis);
    }
}