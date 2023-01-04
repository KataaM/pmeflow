package fr.pmeflow.grc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.pmeflow.activite.model.Collaborateur;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DonneesEntreprise extends AbstractValidatedEntity {

	// ----------- Attributs -----------
	private static final long serialVersionUID = -7558650636949258160L;

	private String oid;
	private String libelle; // obligatoire
	private String email; // obligatoire
	private List<Adresse> adresses; // obligatoire
	private String secteur; // obligatoire
	private String telephone; // obligatoire
	private String fax;
	private String siteWeb;
	private String typeEntreprise; // obligatoire
	private List<String> langues;
	private Collaborateur gestionnaireCompte;
	private String ape; // obligatoire

	// ----------- Methodes -----------
	public static DonneesEntreprise valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<DonneesEntreprise>() {
		});
	}

	@Override
	public String getOid() {
		if (StringUtil.isEmpty(oid)) {
			this.oid = GuidGenerator.getGUID(this);
		}
		return oid;
	}

	@Override
	public void setOid(String oid) {
		this.oid = oid;
	}

	@NotEmpty
	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@NotEmpty
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty
	public List<Adresse> getAdresses() {
		if (CollectionUtil.isEmpty(adresses)) {
			this.adresses = new ArrayList<>();
		}
		return adresses;
	}

	public void setAdresses(List<Adresse> adresses) {
		this.adresses = adresses;
	}

	@NotEmpty
	public String getSecteur() {
		return secteur;
	}

	public void setSecteur(String secteur) {
		this.secteur = secteur;
	}

	@NotEmpty
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getSiteWeb() {
		return siteWeb;
	}

	public void setSiteWeb(String siteWeb) {
		this.siteWeb = siteWeb;
	}

	@NotEmpty
	public String getApe() {
		return ape;
	}

	public void setApe(String ape) {
		this.ape = ape;
	}

	@NotEmpty
	public String getTypeEntreprise() {
		return typeEntreprise;
	}

	public void setTypeEntreprise(String typeEntreprise) {
		this.typeEntreprise = typeEntreprise;
	}

	@NotEmpty
	public List<String> getLangues() {
		if (CollectionUtil.isEmpty(langues)) {
			this.langues = new ArrayList<>();
		}
		return langues;
	}

	public void setLangues(List<String> langues) {
		this.langues = langues;
	}

	public Collaborateur getGestionnaireCompte() {
		return gestionnaireCompte;
	}

	public void setGestionnaireCompte(Collaborateur gestionnaireCompte) {
		this.gestionnaireCompte = gestionnaireCompte;
	}

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@Override
	public int hashCode() {
		final int prime = 42;
		int result = super.hashCode();
		result = prime * result + Objects.hash(libelle, ape, email, adresses, secteur, telephone, fax, siteWeb,
				typeEntreprise, langues, gestionnaireCompte);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DonneesEntreprise other = (DonneesEntreprise) obj;
		return Objects.equals(libelle, other.libelle) && Objects.equals(ape, other.ape)
				&& Objects.equals(email, other.email) && Objects.equals(telephone, other.telephone)
				&& Objects.equals(fax, other.fax) && Objects.equals(siteWeb, other.siteWeb)
				&& Objects.equals(gestionnaireCompte, other.gestionnaireCompte);
	}
}