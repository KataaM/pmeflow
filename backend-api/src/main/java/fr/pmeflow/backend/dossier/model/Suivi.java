/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.dossier.model;

import java.util.Calendar;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Cette classe est l'entite representant un suivi.
 * 
 * @author ludovic.terral
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown=true)
public class Suivi extends AbstractValidatedEntity
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202201172101L;

	private String oid;
    private Calendar date;
	private String contenu;
	
	

    // ----------- Methode -----------
	public Suivi()
	{
	    //no action
	}
	public Suivi(Calendar date, String contenu) 
	{
	    this.date = date;
	    this.contenu = contenu;
	}
	
	
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
    
    
	
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@NotNull
    public Calendar getDate()
    {
        return date;
    }
    public void setDate(Calendar date)
    {
        this.date = date;
    }


    
    @NotNull
    public String getContenu()
    {
        return contenu;
    }
    public void setContenu(String contenu)
    {
        this.contenu = contenu;
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
        result = prime * result + Objects.hash(contenu, date, oid);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Suivi other = (Suivi) obj;
        return Objects.equals(contenu, other.contenu) && Objects.equals(date, other.date)
                && Objects.equals(oid, other.oid);
    }
}