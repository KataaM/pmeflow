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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cette classe représente une adresse d'un etablissement
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdresseEtablissement 
{
    // ----------- Attribut(s) -----------
    private String complementAdresseEtablissement = null;
    private String numeroVoieEtablissement;
    private String indiceRepetitionEtablissement;
    private String typeVoieEtablissement;
    private String libelleVoieEtablissement;
    private String codePostalEtablissement;
    private String libelleCommuneEtablissement;
    private String libelleCommuneEtrangerEtablissement = null;
    private String distributionSpecialeEtablissement = null;
    private String codeCommuneEtablissement;
    private String codeCedexEtablissement = null;
    private String libelleCedexEtablissement = null;
    private String codePaysEtrangerEtablissement = null;
    private String libellePaysEtrangerEtablissement = null;
    
    
        
    // ----------- Methode(s) -----------
    public String getComplementAdresseEtablissement()
    {
        return complementAdresseEtablissement;
    }
    public void setComplementAdresseEtablissement(String complementAdresseEtablissement)
    {
        this.complementAdresseEtablissement = complementAdresseEtablissement;
    }
    
    
    
    public String getNumeroVoieEtablissement()
    {
        return numeroVoieEtablissement;
    }
    public void setNumeroVoieEtablissement(String numeroVoieEtablissement)
    {
        this.numeroVoieEtablissement = numeroVoieEtablissement;
    }
    
    
    
    public String getIndiceRepetitionEtablissement()
    {
        return indiceRepetitionEtablissement;
    }
    public void setIndiceRepetitionEtablissement(String indiceRepetitionEtablissement)
    {
        this.indiceRepetitionEtablissement = indiceRepetitionEtablissement;
    }
    
    
    
    public String getTypeVoieEtablissement()
    {
        return typeVoieEtablissement;
    }
    public void setTypeVoieEtablissement(String typeVoieEtablissement)
    {
        this.typeVoieEtablissement = typeVoieEtablissement;
    }
    
    
    
    public String getLibelleVoieEtablissement()
    {
        return libelleVoieEtablissement;
    }
    public void setLibelleVoieEtablissement(String libelleVoieEtablissement)
    {
        this.libelleVoieEtablissement = libelleVoieEtablissement;
    }
    
    
    
    public String getCodePostalEtablissement()
    {
        return codePostalEtablissement;
    }
    public void setCodePostalEtablissement(String codePostalEtablissement)
    {
        this.codePostalEtablissement = codePostalEtablissement;
    }
    
    
    
    public String getLibelleCommuneEtablissement()
    {
        return libelleCommuneEtablissement;
    }
    public void setLibelleCommuneEtablissement(String libelleCommuneEtablissement)
    {
        this.libelleCommuneEtablissement = libelleCommuneEtablissement;
    }
    
    
    
    public String getLibelleCommuneEtrangerEtablissement()
    {
        return libelleCommuneEtrangerEtablissement;
    }
    public void setLibelleCommuneEtrangerEtablissement(String libelleCommuneEtrangerEtablissement)
    {
        this.libelleCommuneEtrangerEtablissement = libelleCommuneEtrangerEtablissement;
    }
    
    
    
    public String getDistributionSpecialeEtablissement()
    {
        return distributionSpecialeEtablissement;
    }
    public void setDistributionSpecialeEtablissement(String distributionSpecialeEtablissement)
    {
        this.distributionSpecialeEtablissement = distributionSpecialeEtablissement;
    }
    
    
    
    public String getCodeCommuneEtablissement()
    {
        return codeCommuneEtablissement;
    }
    public void setCodeCommuneEtablissement(String codeCommuneEtablissement)
    {
        this.codeCommuneEtablissement = codeCommuneEtablissement;
    }
    
    
    
    public String getCodeCedexEtablissement()
    {
        return codeCedexEtablissement;
    }
    public void setCodeCedexEtablissement(String codeCedexEtablissement)
    {
        this.codeCedexEtablissement = codeCedexEtablissement;
    }
    
    
    
    public String getLibelleCedexEtablissement()
    {
        return libelleCedexEtablissement;
    }
    public void setLibelleCedexEtablissement(String libelleCedexEtablissement)
    {
        this.libelleCedexEtablissement = libelleCedexEtablissement;
    }
    
    
    
    public String getCodePaysEtrangerEtablissement()
    {
        return codePaysEtrangerEtablissement;
    }
    public void setCodePaysEtrangerEtablissement(String codePaysEtrangerEtablissement)
    {
        this.codePaysEtrangerEtablissement = codePaysEtrangerEtablissement;
    }
    
    
    
    public String getLibellePaysEtrangerEtablissement()
    {
        return libellePaysEtrangerEtablissement;
    }
    public void setLibellePaysEtrangerEtablissement(String libellePaysEtrangerEtablissement)
    {
        this.libellePaysEtrangerEtablissement = libellePaysEtrangerEtablissement;
    }
}
