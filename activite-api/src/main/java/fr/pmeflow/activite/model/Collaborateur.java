package fr.pmeflow.activite.model;

import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.pmeflow.activite.model.enums.ProfilCollaborateur;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Collaborateur extends AbstractValidatedEntity {

	// ----------- Attributs -----------
	private static final long serialVersionUID = 4918418908407998058L;

	private String oid;
	private ProfilCollaborateur profil;
	private String nom;
	private String prenom;
	private String motDePasse;

	// ----------- Methodes -----------
	public static Collaborateur valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<Collaborateur>() {
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

	@NotNull
	public ProfilCollaborateur getProfil() {
		if (profil == null) {
			this.profil = ProfilCollaborateur.COLLABORATEUR;
		}
		return profil;
	}

	public void setProfil(ProfilCollaborateur profil) {
		this.profil = profil;
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
	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motdepasse) {
		this.motDePasse = motdepasse;
	}

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@Override
	public int hashCode() {
		final int prime = 18;
		int result = super.hashCode();
		result = prime * result + Objects.hash(profil, nom, prenom, motDePasse);
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
		Collaborateur other = (Collaborateur) obj;
		return Objects.equals(profil, other.profil) && Objects.equals(nom, other.nom)
				&& Objects.equals(prenom, other.prenom) && Objects.equals(motDePasse, other.motDePasse);
	}
}