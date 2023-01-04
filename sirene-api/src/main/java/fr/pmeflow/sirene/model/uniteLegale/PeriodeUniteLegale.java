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

import java.util.Calendar;

/**
 * Cette classe correspond aux informations données mise à jour
 * pendant une période donnée
 * 
 * @author ludovic.terral
 */
public class PeriodeUniteLegale
{
    // ----------- Attribut(s) -----------   
    private Calendar dateFin = null;
    private Calendar dateDebut;
    private String etatAdministratifUniteLegale;
    private boolean changementEtatAdministratifUniteLegale;
    private String nomUniteLegale;
    private boolean changementNomUniteLegale;
    private String nomUsageUniteLegale = null;
    private boolean changementNomUsageUniteLegale;
    private String denominationUniteLegale = null;
    private boolean changementDenominationUniteLegale;
    private String denominationUsuelle1UniteLegale = null;
    private String denominationUsuelle2UniteLegale = null;
    private String denominationUsuelle3UniteLegale = null;
    private boolean changementDenominationUsuelleUniteLegale;
    private String categorieJuridiqueUniteLegale;
    private boolean changementCategorieJuridiqueUniteLegale;
    private String activitePrincipaleUniteLegale;
    private String nomenclatureActivitePrincipaleUniteLegale;
    private boolean changementActivitePrincipaleUniteLegale;
    private String nicSiegeUniteLegale;
    private boolean changementNicSiegeUniteLegale;
    private String economieSocialeSolidaireUniteLegale = null;
    private boolean changementEconomieSocialeSolidaireUniteLegale;
    private String caractereEmployeurUniteLegale;
    private boolean changementCaractereEmployeurUniteLegale;
    
    

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
    
    
    
    public String getEtatAdministratifUniteLegale()
    {
        return etatAdministratifUniteLegale;
    }
    public void setEtatAdministratifUniteLegale(String etatAdministratifUniteLegale)
    {
        this.etatAdministratifUniteLegale = etatAdministratifUniteLegale;
    }
    
    
    
    public boolean isChangementEtatAdministratifUniteLegale()
    {
        return changementEtatAdministratifUniteLegale;
    }
    public void setChangementEtatAdministratifUniteLegale(
            boolean changementEtatAdministratifUniteLegale)
    {
        this.changementEtatAdministratifUniteLegale = changementEtatAdministratifUniteLegale;
    }
    
    
    
    public String getNomUniteLegale()
    {
        return nomUniteLegale;
    }
    public void setNomUniteLegale(String nomUniteLegale)
    {
        this.nomUniteLegale = nomUniteLegale;
    }
    
    
    
    public boolean isChangementNomUniteLegale()
    {
        return changementNomUniteLegale;
    } 
    public void setChangementNomUniteLegale(boolean changementNomUniteLegale)
    {
        this.changementNomUniteLegale = changementNomUniteLegale;
    }
    
    
        
    public String getNomUsageUniteLegale()
    {
        return nomUsageUniteLegale;
    }    
    public void setNomUsageUniteLegale(String nomUsageUniteLegale)
    {
        this.nomUsageUniteLegale = nomUsageUniteLegale;
    }
    
    
    
    public boolean isChangementNomUsageUniteLegale()
    {
        return changementNomUsageUniteLegale;
    }
    public void setChangementNomUsageUniteLegale(boolean changementNomUsageUniteLegale)
    {
        this.changementNomUsageUniteLegale = changementNomUsageUniteLegale;
    }
    
    
    
    public String getDenominationUniteLegale()
    {
        return denominationUniteLegale;
    }
    public void setDenominationUniteLegale(String denominationUniteLegale)
    {
        this.denominationUniteLegale = denominationUniteLegale;
    }
    
    
    
    public boolean isChangementDenominationUniteLegale()
    {
        return changementDenominationUniteLegale;
    }
    public void setChangementDenominationUniteLegale(boolean changementDenominationUniteLegale)
    {
        this.changementDenominationUniteLegale = changementDenominationUniteLegale;
    }
    
    
    
    public String getDenominationUsuelle1UniteLegale()
    {
        return denominationUsuelle1UniteLegale;
    }
    public void setDenominationUsuelle1UniteLegale(String denominationUsuelle1UniteLegale)
    {
        this.denominationUsuelle1UniteLegale = denominationUsuelle1UniteLegale;
    }
    
    
    
    public String getDenominationUsuelle2UniteLegale()
    {
        return denominationUsuelle2UniteLegale;
    }
    public void setDenominationUsuelle2UniteLegale(String denominationUsuelle2UniteLegale)
    {
        this.denominationUsuelle2UniteLegale = denominationUsuelle2UniteLegale;
    }
    
    
    
    public String getDenominationUsuelle3UniteLegale()
    {
        return denominationUsuelle3UniteLegale;
    }
    public void setDenominationUsuelle3UniteLegale(String denominationUsuelle3UniteLegale)
    {
        this.denominationUsuelle3UniteLegale = denominationUsuelle3UniteLegale;
    }
    
    
    
    public boolean isChangementDenominationUsuelleUniteLegale()
    {
        return changementDenominationUsuelleUniteLegale;
    }    
    public void setChangementDenominationUsuelleUniteLegale(
            boolean changementDenominationUsuelleUniteLegale)
    {
        this.changementDenominationUsuelleUniteLegale = changementDenominationUsuelleUniteLegale;
    }
    
    
    
    public String getCategorieJuridiqueUniteLegale()
    {
        return categorieJuridiqueUniteLegale;
    }
    public void setCategorieJuridiqueUniteLegale(String categorieJuridiqueUniteLegale)
    {
        this.categorieJuridiqueUniteLegale = categorieJuridiqueUniteLegale;
    }
    
    
    
    public boolean isChangementCategorieJuridiqueUniteLegale()
    {
        return changementCategorieJuridiqueUniteLegale;
    }
    public void setChangementCategorieJuridiqueUniteLegale(
            boolean changementCategorieJuridiqueUniteLegale)
    {
        this.changementCategorieJuridiqueUniteLegale = changementCategorieJuridiqueUniteLegale;
    }
    
    
    
    public String getActivitePrincipaleUniteLegale()
    {
        return activitePrincipaleUniteLegale;
    }
    public void setActivitePrincipaleUniteLegale(String activitePrincipaleUniteLegale)
    {
        this.activitePrincipaleUniteLegale = activitePrincipaleUniteLegale;
    }
    
    
    
    public String getNomenclatureActivitePrincipaleUniteLegale()
    {
        return nomenclatureActivitePrincipaleUniteLegale;
    }
    public void setNomenclatureActivitePrincipaleUniteLegale(
            String nomenclatureActivitePrincipaleUniteLegale)
    {
        this.nomenclatureActivitePrincipaleUniteLegale = nomenclatureActivitePrincipaleUniteLegale;
    }
    
    
    
    public boolean isChangementActivitePrincipaleUniteLegale()
    {
        return changementActivitePrincipaleUniteLegale;
    }
    public void setChangementActivitePrincipaleUniteLegale(
            boolean changementActivitePrincipaleUniteLegale)
    {
        this.changementActivitePrincipaleUniteLegale = changementActivitePrincipaleUniteLegale;
    }
    
    
    
    public String getNicSiegeUniteLegale()
    {
        return nicSiegeUniteLegale;
    }
    public void setNicSiegeUniteLegale(String nicSiegeUniteLegale)
    {
        this.nicSiegeUniteLegale = nicSiegeUniteLegale;
    }
    
    
    
    public boolean isChangementNicSiegeUniteLegale()
    {
        return changementNicSiegeUniteLegale;
    }
    public void setChangementNicSiegeUniteLegale(boolean changementNicSiegeUniteLegale)
    {
        this.changementNicSiegeUniteLegale = changementNicSiegeUniteLegale;
    }
    
    
    
    public String getEconomieSocialeSolidaireUniteLegale()
    {
        return economieSocialeSolidaireUniteLegale;
    }
    public void setEconomieSocialeSolidaireUniteLegale(String economieSocialeSolidaireUniteLegale)
    {
        this.economieSocialeSolidaireUniteLegale = economieSocialeSolidaireUniteLegale;
    }
    
    
    
    public boolean isChangementEconomieSocialeSolidaireUniteLegale()
    {
        return changementEconomieSocialeSolidaireUniteLegale;
    }
    public void setChangementEconomieSocialeSolidaireUniteLegale(
            boolean changementEconomieSocialeSolidaireUniteLegale)
    {
        this.changementEconomieSocialeSolidaireUniteLegale = changementEconomieSocialeSolidaireUniteLegale;
    }
    
    
    
    public String getCaractereEmployeurUniteLegale()
    {
        return caractereEmployeurUniteLegale;
    }
    public void setCaractereEmployeurUniteLegale(String caractereEmployeurUniteLegale)
    {
        this.caractereEmployeurUniteLegale = caractereEmployeurUniteLegale;
    }
    
    
    
    public boolean isChangementCaractereEmployeurUniteLegale()
    {
        return changementCaractereEmployeurUniteLegale;
    }
    public void setChangementCaractereEmployeurUniteLegale(
            boolean changementCaractereEmployeurUniteLegale)
    {
        this.changementCaractereEmployeurUniteLegale = changementCaractereEmployeurUniteLegale;
    }
}
