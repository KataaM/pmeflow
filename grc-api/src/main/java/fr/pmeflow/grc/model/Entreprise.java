package fr.pmeflow.grc.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.orm.redis.query.RedisSearchValueSanitizer;
import fr.pmeflow.activite.model.Activite;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.grc.model.enums.TypeFacturation;
import fr.pmeflow.opportunite.model.Opportunite;
import redis.clients.jedis.search.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entreprise extends AbstractValidatedEntity implements RedisSearchDao {

	// ----------- Attributs -----------
	private static final long serialVersionUID = -7126409190151866049L;

	private String oid;
	private DonneesEntreprise donneesEntreprise; // not null
	private CoordonneesFinancieres coordsFinancieres; // not null
	private List<TypeFacturation> listeModesFacturation; // not null
	private boolean etat; // not null
	private List<String> tags; // peut etre vide
	private List<Activite> listeActivites;	// peut etre vide
	private List<Opportunite> listeOpportunites;	// peut etre vide

	// ----------- Methodes -----------
	public static Entreprise valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<Entreprise>() {
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
	public DonneesEntreprise getDonneesEntreprise() {
		return donneesEntreprise;
	}

	public void setDonneesEntreprise(DonneesEntreprise donneesEntreprise) {
		this.donneesEntreprise = donneesEntreprise;
	}

	@NotNull
	public CoordonneesFinancieres getCoordsFinancieres() {
		return coordsFinancieres;
	}

	public void setCoordsFinancieres(CoordonneesFinancieres coordsFinancieres) {
		this.coordsFinancieres = coordsFinancieres;
	}

	@NotEmpty
	public List<TypeFacturation> getListeModesFacturation() {
		if (CollectionUtil.isEmpty(listeModesFacturation)) {
			this.listeModesFacturation = new ArrayList<>();
		}
		return listeModesFacturation;
	}

	public void setListeModesFacturation(List<TypeFacturation> listeModesFacturation) {
		this.listeModesFacturation = listeModesFacturation;
	}

	@NotNull
	public boolean getEtat() {
		return etat;
	}

	public void setEtat(boolean etat) {
		this.etat = etat;
	}

	public List<String> getTags() {
		if (CollectionUtil.isEmpty(tags)) {
			this.tags = new ArrayList<>();
		}
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	@NotNull
	public List<Activite> getListeActivites() {
		if (CollectionUtil.isEmpty(listeActivites)) {
			this.listeActivites = new ArrayList<>();
		}
		return listeActivites;
	}
	
	public void setListeActivites(List<Activite> listeActivites) {
		this.listeActivites = listeActivites;
	}
	
	@NotNull
	public List<Opportunite> getListeOpportunites() {
		if (CollectionUtil.isEmpty(listeOpportunites)) {
			this.listeOpportunites = new ArrayList<>();
		}
		return listeOpportunites;
	}
	
	public void setListeOpportunites(List<Opportunite> listeOpportunites) {
		this.listeOpportunites = listeOpportunites;
	}

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public String getKey() {
		return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:" + Entreprise.class.getName() + ":" + oid;
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public Schema getIndexSchema() {
		return new Schema().addSortableTextField("libelleEntreprise", 2).addSortableTextField("tags", 1);
	}

	@Override
	public Map<String, Object> getIndexFieldValues() {
		Map<String, Object> indexFields = new HashMap<>();
		indexFields.put("libelleEntreprise", RedisSearchValueSanitizer.sanitizeValue(donneesEntreprise.getLibelle()));
		indexFields.put("tags", RedisSearchValueSanitizer.sanitizeValue(tags));
		return indexFields;
	}

	@Override
	public long getTTL() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 65;
		int result = super.hashCode();
		result = prime * result + Objects.hash(donneesEntreprise, coordsFinancieres, listeModesFacturation, etat, tags, listeActivites);
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
		Entreprise other = (Entreprise) obj;

		return Objects.equals(donneesEntreprise, other.donneesEntreprise)
				&& Objects.equals(coordsFinancieres, other.coordsFinancieres)
				&& Objects.equals(listeModesFacturation, other.listeModesFacturation);
	}

	@Override
	public ConteneurEvenement validate(String parent, Contexte contexte) throws BusinessException {
		ConteneurEvenement conteneur = super.validate(parent, contexte);

		boolean ok = true;

		/* verifications NOT NULL */
		if (donneesEntreprise == null) {
			conteneur.add(NiveauEvenement.ERROR,
					parent + this.getClass().getCanonicalName() + " : "
							+ "L'objet DonneesEntreprise ne peut pas etre nul!",
					Calendar.getInstance(), contexte, "classe");
			ok = false;
		}

		if (coordsFinancieres == null) {
			conteneur.add(NiveauEvenement.ERROR,
					parent + this.getClass().getCanonicalName() + " : "
							+ "L'objet CoordonneesFinancieres ne peut pas etre nul!",
					Calendar.getInstance(), contexte, "classe");
			ok = false;
		}

		if (ok) {
			conteneur.addAll(donneesEntreprise.validate());
			conteneur.addAll(coordsFinancieres.validate());
		}

		return conteneur;
	}
}