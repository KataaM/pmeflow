package fr.pmeflow.grc.model;

import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoordonneesFinancieres extends AbstractValidatedEntity {

	// ----------- Attributs -----------
	private static final long serialVersionUID = -9034952319962946165L;

	private String oid;
	private String siret;
	private int tva;
	private String iban;
	private String bicSwift;
	private boolean tvaBool;
	private float prix;
	private String conditionsPaiement;

	// ----------- Methodes -----------
	public static CoordonneesFinancieres valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<CoordonneesFinancieres>() {
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
	public String getSiret() {
		return siret;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

	@NotNull
	public int getTva() {
		return tva;
	}

	public void setTva(int tva) {
		this.tva = tva;
	}

	@NotEmpty
	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	@NotEmpty
	public String getBicSwift() {
		return bicSwift;
	}

	public void setBicSwift(String bicSwift) {
		this.bicSwift = bicSwift;
	}

	@NotNull
	public boolean getTvaBool() {
		return tvaBool;
	}

	public void setTvaBool(boolean tvaBool) {
		this.tvaBool = tvaBool;
	}

	@NotNull
	public float getPrix() {
		return prix;
	}

	public void setPrix(float prix) {
		this.prix = prix;
	}

	@NotEmpty
	public String getConditionsPaiement() {
		return conditionsPaiement;
	}

	public void setConditionsPaiement(String conditionsPaiement) {
		this.conditionsPaiement = conditionsPaiement;
	}

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@Override
	public int hashCode() {
		final int prime = 24;
		int result = super.hashCode();
		result = prime * result + Objects.hash(siret, tva, iban, bicSwift, tvaBool, prix, conditionsPaiement);
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
		CoordonneesFinancieres other = (CoordonneesFinancieres) obj;
		return Objects.equals(siret, other.siret) && Objects.equals(iban, other.iban)
				&& Objects.equals(bicSwift, other.bicSwift);
	}
}