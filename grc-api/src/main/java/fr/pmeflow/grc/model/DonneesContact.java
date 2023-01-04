package fr.pmeflow.grc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DonneesContact extends AbstractValidatedEntity {

	// ----------- Attributs -----------
	private static final long serialVersionUID = 3788011192259294967L;

	private String oid;
	private String nom;
	private String prenom;
	private String email;
	private List<Adresse> adresses;
	private String mobile;
	private String telephone;
	private String fax;
	private String siteWeb;
	private String genre;
	private String dateNaissance;
	private List<String> langues;

	// ----------- Methodes -----------
	public static DonneesContact valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<DonneesContact>() {
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
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	@NotEmpty
	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	@NotEmpty
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotNull
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
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	@NotEmpty
	public String getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(String dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	@NotNull
	public List<String> getLangues() {
		if (CollectionUtil.isEmpty(langues)) {
			this.langues = new ArrayList<>();
		}
		return langues;
	}

	public void setLangues(List<String> langues) {
		this.langues = langues;
	}

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@Override
	public int hashCode() {
		final int prime = 42;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(nom, prenom, email, adresses, mobile, telephone, fax, siteWeb, dateNaissance, langues);
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
		DonneesContact other = (DonneesContact) obj;
		return Objects.equals(nom, other.nom) && Objects.equals(prenom, other.prenom)
				&& Objects.equals(prenom, other.prenom)
				&& (Objects.equals(email, other.email) || Objects.equals(mobile, other.mobile)
						|| Objects.equals(telephone, other.telephone) || Objects.equals(fax, other.fax))
				&& Objects.equals(dateNaissance, other.dateNaissance);
	}
}