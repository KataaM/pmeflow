package fr.pmeflow.activite.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.model.Contexte;
import fr.lixbox.common.model.enumeration.NiveauEvenement;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import fr.pmeflow.commons.PmeflowConstant;
import redis.clients.jedis.search.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Activite extends AbstractValidatedEntity implements RedisSearchDao {

	// ----------- Attributs -----------
	private static final long serialVersionUID = -7038283664639431457L;

	private String oid;
	private Collaborateur collaborateur; // not null
	private String date; // sera genere côté serveur
	private String commentaire; // peut etre vide
	private String oidCible; // not null
	private ReferenceAction refAction; // not null

	// ----------- Methodes -----------
	public static Activite valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<Activite>() {
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
	public Collaborateur getCollaborateur() {
		return collaborateur;
	}

	public void setCollaborateur(Collaborateur collaborateur) {
		this.collaborateur = collaborateur;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	
	@NotEmpty
	public String getOidCible() {
		return oidCible;
	}

	public void setOidCible(String oidCible) {
		this.oidCible = oidCible;
	}
	
	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public String getKey() {
		return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:" + Activite.class.getName() + ":" + oid;
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public Schema getIndexSchema() {
		return new Schema().addSortableTextField("commentaire", 1).addSortableTextField("date", 2)
				.addSortableTextField("idCollaborateur", 1).addSortableTextField("oidCible", 2);
	}

	@Override
	public Map<String, Object> getIndexFieldValues() {
		Map<String, Object> indexFields = new HashMap<>();
		indexFields.put("commentaire", RedisSearchValueSanitizer.sanitizeValue(commentaire));
		indexFields.put("date", RedisSearchValueSanitizer.sanitizeValue(date));
		indexFields.put("oidCible", RedisSearchValueSanitizer.sanitizeValue(oidCible));
		indexFields.put("idCollaborateur", RedisSearchValueSanitizer.sanitizeValue(collaborateur.getOid()));
		return indexFields;
	}

	@Override
	public long getTTL() {
		return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 74;
		int result = super.hashCode();
		result = prime * result + Objects.hash(collaborateur, date, commentaire, oidCible, refAction);
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
		Activite other = (Activite) obj;
		return Objects.equals(collaborateur, other.collaborateur) && Objects.equals(date, other.date)
				&& Objects.equals(commentaire, other.commentaire)
				&& Objects.equals(oidCible, other.oidCible)
				&& Objects.equals(refAction, other.refAction);
	}
	
	@Override
	public ConteneurEvenement validate(String parent, Contexte contexte) throws BusinessException {
		ConteneurEvenement conteneur = super.validate(parent, contexte);

		boolean ok = true;

		/* verifications NOT NULL */
		if (collaborateur == null) {
			conteneur.add(NiveauEvenement.ERROR,
					parent + this.getClass().getCanonicalName() + " : "
							+ "L'objet Collaborateur ne peut pas etre nul!",
					Calendar.getInstance(), contexte, "classe");
			ok = false;
		}
		
		if (ok) {
			conteneur.addAll(collaborateur.validate());			
		}

		return conteneur;
	}
}