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
package fr.pmeflow.iam.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.pmeflow.iam.model.enumeration.CategorieEntreprise;
import fr.pmeflow.iam.model.enumeration.EtatOrganisation;
import fr.pmeflow.iam.model.enumeration.Nature;

/**
 * Cette classe est l'entite representant une organisation.
 * 
 * @author virgile.de-lacerda
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Organisation extends AbstractValidatedEntity
{
    // ----------- Attributs -----------
    private static final long serialVersionUID = 201505121107L;
    
    public static final String STATE_LABEL = "STATE";
    public static final String NATURE_LABEL = "NATURE";
    public static final String CAT_JURIDIQUE_LABEL ="CAT_JURIDIQUE";
    public static final String ACT_EXERC_LABEL ="ACT_EXERC";
    public static final String CAT_ENTREPRISE_LABEL = "CAT_ENTREPRISE";
    public static final String EFFECTIF_LABEL = "EFFECTIF";
    public static final String IDENTIFIANTS_LABEL = "IDENTIFIANTS";
    public static final String ATTRIBUTES_LABEL = "ATTRIBUTES";
    public static final String MOYENS_LABEL = "MOYENS";
    public static final String SIGLE_LABEL = "SIGLE";
    public static final String PARENT_ID_LABEL = "PARENT_ID";
    
    private String oid;
	private String commentaire;
	private EtatOrganisation etat=EtatOrganisation.INACTIF;
	private String nom;
    private Nature nature=Nature.INTERNE;
    private String categorieJuridique;
    private String activitePrincipale;
    private String effectif;
    private CategorieEntreprise categorieEntreprise;
    private String sigle;
    private String parentId;
	
    private List<Organisation> organisations;
	private Map<String, String> identifiants;
	private Map<String, String> moyens;
	private Map<String, String> attributes;
    
    

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
    
        
    
    public Nature getNature()
    {
        if (this.nature == null)
        {
            this.nature = Nature.INTERNE; 
        }
        return nature;
    }
    public void setNature(Nature origine)
    {
        this.nature = origine;
    }
	
	
    
	public String getNom()
    {
        return nom;
    }
    public void setNom(String nom)
    {
        this.nom = nom;
    }
    
    
    
	@Size(max=256)
	public String getCommentaire()
	{
		return commentaire;
	}
	public void setCommentaire(String commentaire)
	{
		this.commentaire = commentaire;
	}

	
	
	@NotNull
	public EtatOrganisation getEtat()
	{
		return etat;
	}
	public void setEtat(EtatOrganisation etat)
	{
		this.etat = etat;
	}
		
	
	
	public String getCategorieJuridique()
    {
        return categorieJuridique;
    }
    public void setCategorieJuridique(String categorieJuridique)
    {
        this.categorieJuridique = categorieJuridique;
    }
    
    
    
    public String getActivitePrincipale()
    {
        return activitePrincipale;
    }
    public void setActivitePrincipale(String activitePrincipale)
    {
        this.activitePrincipale = activitePrincipale;
    }
    
    
    
    public String getEffectif()
    {
        return effectif;
    }
    public void setEffectif(String effectif)
    {
        this.effectif = effectif;
    }
    
    
    
    public CategorieEntreprise getCategorieEntreprise()
    {
        return categorieEntreprise;
    }
    public void setCategorieEntreprise(CategorieEntreprise categorieEntreprise)
    {
        this.categorieEntreprise = categorieEntreprise;
    }
    
    
    
    public String getSigle()
    {
        return sigle;
    }
    public void setSigle(String sigle)
    {
        this.sigle = sigle;
    }
    
    
    
    public String getParentId()
    {
        return parentId;
    }
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }
    

    
    public List<Organisation> getOrganisations()
    {
        if (organisations==null)
        {
            organisations = new ArrayList<>();
        }
        return organisations;
    }
    public void setOrganisations(List<Organisation> organisations)
    {
        this.organisations = organisations;
    }
    
	

	public Map<String, String> getMoyens()
	{
        if (moyens==null)
        {
            moyens = new HashMap<>();
        }
		return this.moyens;
	}
	public void setMoyens(Map<String,String> moyens)
	{
		this.moyens = moyens;
	}
	
	

	public Map<String, String> getIdentifiants()
	{
        if (identifiants==null)
        {
            identifiants = new HashMap<>();
        }
		return this.identifiants;
	}
	public void setIdentifiants(Map<String, String> identifiants)
	{
		this.identifiants = identifiants;
	}
	
	

	public Map<String, String> getAttributes()
    {
        if (attributes==null)
        {
            attributes = new HashMap<>();
        }
        return attributes;
    }
    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
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
        result = prime * result + Objects.hash(activitePrincipale, attributes, categorieEntreprise,
                categorieJuridique, commentaire, effectif, etat, identifiants, moyens, nature, nom,
                oid, organisations, parentId, sigle);
        return result;
    }
    
    
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Organisation other = (Organisation) obj;
        return Objects.equals(activitePrincipale, other.activitePrincipale)
                && Objects.equals(attributes, other.attributes)
                && categorieEntreprise == other.categorieEntreprise
                && Objects.equals(categorieJuridique, other.categorieJuridique)
                && Objects.equals(commentaire, other.commentaire)
                && Objects.equals(effectif, other.effectif) && etat == other.etat
                && Objects.equals(identifiants, other.identifiants)
                && Objects.equals(moyens, other.moyens) && nature == other.nature
                && Objects.equals(nom, other.nom) && Objects.equals(oid, other.oid)
                && Objects.equals(organisations, other.organisations)
                && Objects.equals(parentId, other.parentId) && Objects.equals(sigle, other.sigle);
    }
}