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
package fr.pmeflow.sirene.model.etablissement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.pmeflow.sirene.model.uniteLegale.UniteLegale;

/**
 * Cette classe donne les informations d'un établissement d'une unité légale.
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Etablissement 
{
    // ----------- Attribut(s) -----------
    private String siren;
    private String nic;
    private String siret;
    private String statutDiffusionEtablissement;
    private Calendar dateCreationEtablissement;
    private String trancheEffectifsEtablissement = null;
    private String anneeEffectifsEtablissement = null;
    private String activitePrincipaleRegistreMetiersEtablissement = null;
    private Calendar dateDernierTraitementEtablissement;
    private boolean etablissementSiege;
    private float nombrePeriodesEtablissement;
    UniteLegale uniteLegale;
    AdresseEtablissement adresseEtablissement;
    List<PeriodeEtablissement> periodesEtablissement = new ArrayList<>();
    
    
    
    // ----------- Methode(s) -----------
    public String getSiren()
    {
        return siren;
    }
    public void setSiren(String siren)
    {
        this.siren = siren;
    }
    
    
    
    public String getNic()
    {
        return nic;
    }
    public void setNic(String nic)
    {
        this.nic = nic;
    }
    
    
    
    public String getSiret()
    {
        return siret;
    }
    public void setSiret(String siret)
    {
        this.siret = siret;
    }
    
    
    
    public String getStatutDiffusionEtablissement()
    {
        return statutDiffusionEtablissement;
    }
    public void setStatutDiffusionEtablissement(String statutDiffusionEtablissement)
    {
        this.statutDiffusionEtablissement = statutDiffusionEtablissement;
    }
    
    
    
    public Calendar getDateCreationEtablissement()
    {
        return dateCreationEtablissement;
    }
    public void setDateCreationEtablissement(Calendar dateCreationEtablissement)
    {
        this.dateCreationEtablissement = dateCreationEtablissement;
    }
    
    
    
    public String getTrancheEffectifsEtablissement()
    {
        return trancheEffectifsEtablissement;
    }
    public void setTrancheEffectifsEtablissement(String trancheEffectifsEtablissement)
    {
        this.trancheEffectifsEtablissement = trancheEffectifsEtablissement;
    }
    
    
    
    public String getAnneeEffectifsEtablissement()
    {
        return anneeEffectifsEtablissement;
    }
    public void setAnneeEffectifsEtablissement(String anneeEffectifsEtablissement)
    {
        this.anneeEffectifsEtablissement = anneeEffectifsEtablissement;
    }
    
    
    
    public String getActivitePrincipaleRegistreMetiersEtablissement()
    {
        return activitePrincipaleRegistreMetiersEtablissement;
    }
    public void setActivitePrincipaleRegistreMetiersEtablissement(
            String activitePrincipaleRegistreMetiersEtablissement)
    {
        this.activitePrincipaleRegistreMetiersEtablissement = activitePrincipaleRegistreMetiersEtablissement;
    }
    
    
    
    public Calendar getDateDernierTraitementEtablissement()
    {
        return dateDernierTraitementEtablissement;
    }
    public void setDateDernierTraitementEtablissement(Calendar dateDernierTraitementEtablissement)
    {
        this.dateDernierTraitementEtablissement = dateDernierTraitementEtablissement;
    }
    
    
    
    public boolean isEtablissementSiege()
    {
        return etablissementSiege;
    }
    public void setEtablissementSiege(boolean etablissementSiege)
    {
        this.etablissementSiege = etablissementSiege;
    }
    
    
    
    public float getNombrePeriodesEtablissement()
    {
        return nombrePeriodesEtablissement;
    }
    public void setNombrePeriodesEtablissement(float nombrePeriodesEtablissement)
    {
        this.nombrePeriodesEtablissement = nombrePeriodesEtablissement;
    }
    
    
    
    public UniteLegale getUniteLegale()
    {
        return uniteLegale;
    }
    public void setUniteLegale(UniteLegale uniteLegale)
    {
        this.uniteLegale = uniteLegale;
    }
    
    
    
    public AdresseEtablissement getAdresseEtablissement()
    {
        return adresseEtablissement;
    }
    public void setAdresseEtablissement(AdresseEtablissement adresseEtablissement)
    {
        this.adresseEtablissement = adresseEtablissement;
    }
    
    
    
    public List<PeriodeEtablissement> getPeriodesEtablissement()
    {
        return periodesEtablissement;
    }
    public void setPeriodesEtablissement(List<PeriodeEtablissement> periodesEtablissement)
    {
        this.periodesEtablissement = periodesEtablissement;
    }
}