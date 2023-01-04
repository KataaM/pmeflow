package fr.pmeflow.grc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import fr.pmeflow.commons.PmeflowConstant;
import redis.clients.jedis.search.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Adresse extends AbstractValidatedEntity implements RedisSearchDao {

	// ----------- Attributs -----------
	private static final long serialVersionUID = 8690798708039811873L;

	private String oid;
	private int codePostal;
	private int numero;
	private String rue;
	private String ville;
	private String pays;
	private String departement;

	// ----------- Methodes -----------
	public static Adresse valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<Adresse>() {
		});
	}

	@Override
	public String getOid() {
		return oid;
	}

	@Override
	public void setOid(String oid) {
		this.oid = oid;
	}

	@NotNull
	@NotEmpty
	public int getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(int codePostal) {
		this.codePostal = codePostal;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@NotNull
	@NotEmpty
	public String getRue() {
		return rue;
	}

	public void setRue(String rue) {
		this.rue = rue;
	}

	@NotNull
	@NotEmpty
	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	@NotNull
	@NotEmpty
	public String getPays() {
		return pays;
	}

	public void setPays(String pays) {
		this.pays = pays;
	}

	@NotNull
	@NotEmpty
	public String getDepartement() {
		return departement;
	}

	public void setDepartement(String departement) {
		this.departement = departement;
	}

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public String getKey() {
		return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:" + Adresse.class.getName() + ":" + oid;
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public Schema getIndexSchema() {
		return new Schema().addSortableNumericField("codePostal").addSortableNumericField("numero")
				.addSortableTextField("rue", 1).addSortableTextField("ville", 1).addSortableTextField("pays", 1)
				.addSortableTextField("departement", 1);
	}

	@Override
	public Map<String, Object> getIndexFieldValues() {
		Map<String, Object> indexFields = new HashMap<>();
		indexFields.put("codePostal", RedisSearchValueSanitizer.sanitizeValue(codePostal));
		indexFields.put("numero", RedisSearchValueSanitizer.sanitizeValue(numero));
		indexFields.put("rue", RedisSearchValueSanitizer.sanitizeValue(rue));
		indexFields.put("ville", RedisSearchValueSanitizer.sanitizeValue(ville));
		indexFields.put("pays", RedisSearchValueSanitizer.sanitizeValue(pays));
		indexFields.put("departement", RedisSearchValueSanitizer.sanitizeValue(departement));
		return indexFields;
	}

	@Override
	public long getTTL() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 84;
		int result = super.hashCode();
		result = prime * result + Objects.hash(codePostal, numero, rue, ville, pays, departement);
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
		Adresse other = (Adresse) obj;
		return Objects.equals(codePostal, other.codePostal) && Objects.equals(numero, other.numero)
				&& Objects.equals(rue, other.rue) && Objects.equals(ville, other.ville)
				&& Objects.equals(pays, other.pays) && Objects.equals(departement, other.departement);
	}
}