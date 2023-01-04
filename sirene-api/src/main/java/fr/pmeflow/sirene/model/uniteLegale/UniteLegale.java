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
package fr.pmeflow.sirene.model.uniteLegale;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cette classe correspond à une unité légale (entreprise ou établissement)
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniteLegale
{
    // ----------- Attribut(s) -----------
    
    private String denominationUniteLegale;
    private String nomUniteLegale;
    private String prenomUsuelUniteLegale;
    private String activitePrincipaleUniteLegale;
    private String categorieJuridiqueUniteLegale;
    private String etatAdministratifUniteLegale;
    private String siren;
    private String statutDiffusionUniteLegale;
    private String dateCreationUniteLegale;
    private String sigleUniteLegale = null;
    private String sexeUniteLegale;
    private String prenom1UniteLegale;
    private String prenom2UniteLegale;
    private String prenom3UniteLegale;
    private String prenom4UniteLegale = null;
    private String pseudonymeUniteLegale = null;
    private String identifiantAssociationUniteLegale = null;
    private String trancheEffectifsUniteLegale = null;
    private String anneeEffectifsUniteLegale = null;
    private String dateDernierTraitementUniteLegale;
    private float nombrePeriodesUniteLegale;
    private String categorieEntreprise = null;
    private String anneeCategorieEntreprise = null;
    List<PeriodeUniteLegale> periodesUniteLegale = new ArrayList<>();
    

    
    // ----------- Methode(s) -----------
    public String getSiren()
    {
        return siren;
    }
    public void setSiren(String siren)
    {
        this.siren = siren;
    }
      
    
    
    public String getDenominationUniteLegale()
    {
        return denominationUniteLegale;
    }
    public void setDenominationUniteLegale(String denominationUniteLegale)
    {
        this.denominationUniteLegale = denominationUniteLegale;
    }
    
    
    
    public String getNomUniteLegale()
    {
        return nomUniteLegale;
    }
    public void setNomUniteLegale(String nomUniteLegale)
    {
        this.nomUniteLegale = nomUniteLegale;
    }
    
    
    
    public String getCategorieJuridiqueUniteLegale()
    {
        return categorieJuridiqueUniteLegale;
    }
    public void setCategorieJuridiqueUniteLegale(String categorieJuridiqueUniteLegale)
    {
        this.categorieJuridiqueUniteLegale = categorieJuridiqueUniteLegale;
    }
    
    
    
    public String getActivitePrincipaleUniteLegale()
    {
        return activitePrincipaleUniteLegale;
    }
    public void setActivitePrincipaleUniteLegale(String activitePrincipaleUniteLegale)
    {
        this.activitePrincipaleUniteLegale = activitePrincipaleUniteLegale;
    }
    
    
    
    public String getStatutDiffusionUniteLegale()
    {
        return statutDiffusionUniteLegale;
    }
    public void setStatutDiffusionUniteLegale(String statutDiffusionUniteLegale)
    {
        this.statutDiffusionUniteLegale = statutDiffusionUniteLegale;
    }
    
    
    
    public String getDateCreationUniteLegale()
    {
        return dateCreationUniteLegale;
    }
    public void setDateCreationUniteLegale(String dateCreationUniteLegale)
    {
        this.dateCreationUniteLegale = dateCreationUniteLegale;
    }
    
    
    
    public String getSigleUniteLegale()
    {
        return sigleUniteLegale;
    }
    public void setSigleUniteLegale(String sigleUniteLegale)
    {
        this.sigleUniteLegale = sigleUniteLegale;
    }
    
    
    
    public String getSexeUniteLegale()
    {
        return sexeUniteLegale;
    }
    public void setSexeUniteLegale(String sexeUniteLegale)
    {
        this.sexeUniteLegale = sexeUniteLegale;
    }
    
    
    
    public String getPrenom1UniteLegale()
    {
        return prenom1UniteLegale;
    }
    public void setPrenom1UniteLegale(String prenom1UniteLegale)
    {
        this.prenom1UniteLegale = prenom1UniteLegale;
    }
    
    
    
    public String getPrenom2UniteLegale()
    {
        return prenom2UniteLegale;
    }
    public void setPrenom2UniteLegale(String prenom2UniteLegale)
    {
        this.prenom2UniteLegale = prenom2UniteLegale;
    }
    
    
    
    public String getPrenom3UniteLegale()
    {
        return prenom3UniteLegale;
    }
    public void setPrenom3UniteLegale(String prenom3UniteLegale)
    {
        this.prenom3UniteLegale = prenom3UniteLegale;
    }
    
    
    
    public String getPrenom4UniteLegale()
    {
        return prenom4UniteLegale;
    }
    public void setPrenom4UniteLegale(String prenom4UniteLegale)
    {
        this.prenom4UniteLegale = prenom4UniteLegale;
    }
    
    
    
    public String getPrenomUsuelUniteLegale()
    {
        return prenomUsuelUniteLegale;
    }
    public void setPrenomUsuelUniteLegale(String prenomUsuelUniteLegale)
    {
        this.prenomUsuelUniteLegale = prenomUsuelUniteLegale;
    }
    
    
    
    public String getPseudonymeUniteLegale()
    {
        return pseudonymeUniteLegale;
    }
    public void setPseudonymeUniteLegale(String pseudonymeUniteLegale)
    {
        this.pseudonymeUniteLegale = pseudonymeUniteLegale;
    }
    
    
    
    public String getIdentifiantAssociationUniteLegale()
    {
        return identifiantAssociationUniteLegale;
    }
    public void setIdentifiantAssociationUniteLegale(String identifiantAssociationUniteLegale)
    {
        this.identifiantAssociationUniteLegale = identifiantAssociationUniteLegale;
    }
    
    
    
    public String getTrancheEffectifsUniteLegale()
    {
        return trancheEffectifsUniteLegale;
    }
    public void setTrancheEffectifsUniteLegale(String trancheEffectifsUniteLegale)
    {
        this.trancheEffectifsUniteLegale = trancheEffectifsUniteLegale;
    }
    
    
    
    public String getAnneeEffectifsUniteLegale()
    {
        return anneeEffectifsUniteLegale;
    }
    public void setAnneeEffectifsUniteLegale(String anneeEffectifsUniteLegale)
    {
        this.anneeEffectifsUniteLegale = anneeEffectifsUniteLegale;
    }
    
    
    
    public String getDateDernierTraitementUniteLegale()
    {
        return dateDernierTraitementUniteLegale;
    }
    public void setDateDernierTraitementUniteLegale(String dateDernierTraitementUniteLegale)
    {
        this.dateDernierTraitementUniteLegale = dateDernierTraitementUniteLegale;
    }
    
    
    
    public float getNombrePeriodesUniteLegale()
    {
        return nombrePeriodesUniteLegale;
    }
    public void setNombrePeriodesUniteLegale(float nombrePeriodesUniteLegale)
    {
        this.nombrePeriodesUniteLegale = nombrePeriodesUniteLegale;
    }
    
    
    
    public String getCategorieEntreprise()
    {
        return categorieEntreprise;
    }
    public void setCategorieEntreprise(String categorieEntreprise)
    {
        this.categorieEntreprise = categorieEntreprise;
    }
    
    
    
    public String getAnneeCategorieEntreprise()
    {
        return anneeCategorieEntreprise;
    }
    public void setAnneeCategorieEntreprise(String anneeCategorieEntreprise)
    {
        this.anneeCategorieEntreprise = anneeCategorieEntreprise;
    }
    
    
    
    public List<PeriodeUniteLegale> getPeriodesUniteLegale()
    {
        return periodesUniteLegale;
    }
    public void setPeriodesUniteLegale(List<PeriodeUniteLegale> periodesUniteLegale)
    {
        this.periodesUniteLegale = periodesUniteLegale;
    }
    
    
    
    public String getEtatAdministratifUniteLegale()
    {
        return etatAdministratifUniteLegale;
    }
    public void setEtatAdministratifUniteLegale(String etatAdministratifUniteLegale)
    {
        this.etatAdministratifUniteLegale = etatAdministratifUniteLegale;
    }
}