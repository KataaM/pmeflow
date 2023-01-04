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

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cette classe correspond aux informations données mise à jour
 * pendant une période donnée
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodeEtablissement
{
    // ----------- Attribut(s) -----------
    private Calendar dateFin = null;
    private Calendar dateDebut;
    private String etatAdministratifEtablissement;
    private boolean changementEtatAdministratifEtablissement;
    private String enseigne1Etablissement = null;
    private String enseigne2Etablissement = null;
    private String enseigne3Etablissement = null;
    private boolean changementEnseigneEtablissement;
    private String denominationUsuelleEtablissement = null;
    private boolean changementDenominationUsuelleEtablissement;
    private String activitePrincipaleEtablissement;
    private String nomenclatureActivitePrincipaleEtablissement;
    private boolean changementActivitePrincipaleEtablissement;
    private String caractereEmployeurEtablissement;
    private boolean changementCaractereEmployeurEtablissement;
    
    
    
    // ----------- Methode(s) -----------
    public Calendar getDateFin()
    {
        return dateFin;
    }
    public void setDateFin(Calendar dateFin)
    {
        this.dateFin = dateFin;
    }
    
    
    
    public Calendar getDateDebut()
    {
        return dateDebut;
    }
    public void setDateDebut(Calendar dateDebut)
    {
        this.dateDebut = dateDebut;
    }
    
    
    
    public String getEtatAdministratifEtablissement()
    {
        return etatAdministratifEtablissement;
    }
    public void setEtatAdministratifEtablissement(String etatAdministratifEtablissement)
    {
        this.etatAdministratifEtablissement = etatAdministratifEtablissement;
    }
    
    
    
    public boolean isChangementEtatAdministratifEtablissement()
    {
        return changementEtatAdministratifEtablissement;
    }
    public void setChangementEtatAdministratifEtablissement(
            boolean changementEtatAdministratifEtablissement)
    {
        this.changementEtatAdministratifEtablissement = changementEtatAdministratifEtablissement;
    }
    
    
    
    public String getEnseigne1Etablissement()
    {
        return enseigne1Etablissement;
    }
    public void setEnseigne1Etablissement(String enseigne1Etablissement)
    {
        this.enseigne1Etablissement = enseigne1Etablissement;
    }
    
    
    
    public String getEnseigne2Etablissement()
    {
        return enseigne2Etablissement;
    }
    public void setEnseigne2Etablissement(String enseigne2Etablissement)
    {
        this.enseigne2Etablissement = enseigne2Etablissement;
    }
    
    
    
    public String getEnseigne3Etablissement()
    {
        return enseigne3Etablissement;
    }
    public void setEnseigne3Etablissement(String enseigne3Etablissement)
    {
        this.enseigne3Etablissement = enseigne3Etablissement;
    }
    
    
    
    public boolean isChangementEnseigneEtablissement()
    {
        return changementEnseigneEtablissement;
    }
    public void setChangementEnseigneEtablissement(boolean changementEnseigneEtablissement)
    {
        this.changementEnseigneEtablissement = changementEnseigneEtablissement;
    }
    
    
    
    public String getDenominationUsuelleEtablissement()
    {
        return denominationUsuelleEtablissement;
    }
    public void setDenominationUsuelleEtablissement(String denominationUsuelleEtablissement)
    {
        this.denominationUsuelleEtablissement = denominationUsuelleEtablissement;
    }
    
    
    
    public boolean isChangementDenominationUsuelleEtablissement()
    {
        return changementDenominationUsuelleEtablissement;
    }
    public void setChangementDenominationUsuelleEtablissement(
            boolean changementDenominationUsuelleEtablissement)
    {
        this.changementDenominationUsuelleEtablissement = changementDenominationUsuelleEtablissement;
    }
    
    
    
    public String getActivitePrincipaleEtablissement()
    {
        return activitePrincipaleEtablissement;
    }
    public void setActivitePrincipaleEtablissement(String activitePrincipaleEtablissement)
    {
        this.activitePrincipaleEtablissement = activitePrincipaleEtablissement;
    }
    
    
    
    public String getNomenclatureActivitePrincipaleEtablissement()
    {
        return nomenclatureActivitePrincipaleEtablissement;
    }
    public void setNomenclatureActivitePrincipaleEtablissement(
            String nomenclatureActivitePrincipaleEtablissement)
    {
        this.nomenclatureActivitePrincipaleEtablissement = nomenclatureActivitePrincipaleEtablissement;
    }
    
    
    
    public boolean isChangementActivitePrincipaleEtablissement()
    {
        return changementActivitePrincipaleEtablissement;
    }
    public void setChangementActivitePrincipaleEtablissement(
            boolean changementActivitePrincipaleEtablissement)
    {
        this.changementActivitePrincipaleEtablissement = changementActivitePrincipaleEtablissement;
    }
    
    
    
    public String getCaractereEmployeurEtablissement()
    {
        return caractereEmployeurEtablissement;
    }
    public void setCaractereEmployeurEtablissement(String caractereEmployeurEtablissement)
    {
        this.caractereEmployeurEtablissement = caractereEmployeurEtablissement;
    }
    
    
    
    public boolean isChangementCaractereEmployeurEtablissement()
    {
        return changementCaractereEmployeurEtablissement;
    }
    public void setChangementCaractereEmployeurEtablissement(
            boolean changementCaractereEmployeurEtablissement)
    {
        this.changementCaractereEmployeurEtablissement = changementCaractereEmployeurEtablissement;
    }
}
