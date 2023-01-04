package fr.pmeflow.activite.model;

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
import fr.pmeflow.activite.model.enums.TypeDomaine;
import fr.pmeflow.commons.PmeflowConstant;
import redis.clients.jedis.search.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceAction extends AbstractValidatedEntity {

	// ----------- Attributs -----------
	private static final long serialVersionUID = 7145118242964767666L;

	private String oid;
	private TypeDomaine typeDomaine; // not null
	private String evenement; // not null
	private String icone; // peut etre vide

	// ----------- Methodes -----------
	public static ReferenceAction valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<ReferenceAction>() {
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
	public TypeDomaine getTypeDomaine() {
		return typeDomaine;
	}

	public void setTypeDomaine(TypeDomaine typeDomaine) {
		this.typeDomaine = typeDomaine;
	}
	
	@NotEmpty
	public String getEvenement() {
		return evenement;
	}

	public void setEvenement(String evenement) {
		this.evenement = evenement;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}
	
	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@Override
	public int hashCode() {
		final int prime = 56;
		int result = super.hashCode();
		result = prime * result + Objects.hash(typeDomaine, evenement, icone);
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
		ReferenceAction other = (ReferenceAction) obj;
		return Objects.equals(typeDomaine, other.typeDomaine) && Objects.equals(evenement, other.evenement)
				&& Objects.equals(icone, other.icone);
	}
}