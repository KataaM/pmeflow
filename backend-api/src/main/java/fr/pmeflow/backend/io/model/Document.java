/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.io.model;

import java.util.Calendar;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.iam.model.Profil;
import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Cette classe est l'entite representant un document.
 * 
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Document extends AbstractValidatedEntity
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202201172101L;

	private String oid;
	private Calendar dateDepot;
    private String libelle;
	private String mimetype;
    private Profil profil;
	
	

    // ----------- Methode -----------
	@Override
    public String getOid()
    {
	    if (StringUtil.isEmpty(oid))
	    {
	        this.oid = GuidGenerator.getGUID(this);
	    }
        return oid;
    }
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
    
    

    @NotEmpty
    public Calendar getDateDepot()
    {
        return dateDepot;
    }
    public void setDateDepot(Calendar dateDepot)
    {
        this.dateDepot = dateDepot;
    }
    
    
    
    public String getMimetype()
    {
        return mimetype;
    }
    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }


    
    @NotNull
    public Profil getProfil()
    {
        return profil;
    }
    public void setProfil(Profil profil)
    {
        this.profil = profil;
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
        result = prime * result + Objects.hash(dateDepot, libelle, mimetype, oid, profil);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Document other = (Document) obj;
        return Objects.equals(dateDepot, other.dateDepot) && Objects.equals(libelle, other.libelle)
                && Objects.equals(mimetype, other.mimetype) && Objects.equals(oid, other.oid)
                && Objects.equals(profil, other.profil);
    }
}