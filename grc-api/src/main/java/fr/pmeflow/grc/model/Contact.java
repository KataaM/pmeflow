package fr.pmeflow.grc.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import redis.clients.jedis.search.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact extends AbstractValidatedEntity implements RedisSearchDao {

	// ----------- Attributs -----------
	private static final long serialVersionUID = -7038283664639431457L;

	private String oid;
	private DonneesContact donneesPerso; // not null
	private CoordonneesFinancieres coordsFinancieres; // not null
	private Entreprise entreprise; // peut etre vide
	private List<Contact> contacts; // peut etre vide
	private List<String> tags; // peut etre vide
	private boolean etatSupprime; // not null
	private boolean etatDesactive; // not null
	private List<Activite> listeActivites;	// peut etre vide

	// ----------- Methodes -----------
	public static Contact valueOf(String json) {
		return JsonUtil.transformJsonToObject(json, new TypeReference<Contact>() {
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

	public DonneesContact getDonneesPerso() {
		return donneesPerso;
	}

	public void setDonneesPerso(DonneesContact donneesPerso) {
		this.donneesPerso = donneesPerso;
	}

	public CoordonneesFinancieres getCoordsFinancieres() {
		return coordsFinancieres;
	}

	public void setCoordsFinancieres(CoordonneesFinancieres coordsFinancieres) {
		this.coordsFinancieres = coordsFinancieres;
	}

	public Entreprise getEntreprise() {
		return entreprise;
	}

	public void setEntreprise(Entreprise entreprise) {
		this.entreprise = entreprise;
	}

	@NotNull
	public List<Contact> getContacts() {
		if (CollectionUtil.isEmpty(contacts)) {
			this.contacts = new ArrayList<>();
		}
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
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
	public boolean getEtatSupprime() {
		return etatSupprime;
	}

	public void setEtatSupprime(boolean etatSupprime) {
		this.etatSupprime = etatSupprime;
	}
	
	@NotNull
	public boolean getEtatDesactive() {
		return etatDesactive;
	}

	public void setEtatDesactive(boolean etatDesactive) {
		this.etatDesactive = etatDesactive;
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

	@Override
	public String toString() {
		return JsonUtil.transformObjectToJson(this, false);
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public String getKey() {
		return PmeflowConstant.PMEFLOW_CODE + ":OBJECT:" + Contact.class.getName() + ":" + oid;
	}

	@JsonIgnore
	@XmlTransient
	@Override
	public Schema getIndexSchema() {
		return new Schema().addSortableTextField("tags", 1).addTextField("entrepriseId", 2)
				.addTextField("etatSupprime", 3).addTextField("etatDesactive", 2)
				.addTextField("nom", 1).addTextField("prenom", 1).addTextField("email", 3);
	}

	@Override
	public Map<String, Object> getIndexFieldValues() {
		Map<String, Object> indexFields = new HashMap<>();
		indexFields.put("tags", RedisSearchValueSanitizer.sanitizeValue(tags));
		indexFields.put("entrepriseId", RedisSearchValueSanitizer.sanitizeValue(entreprise.getOid()));
		indexFields.put("etatSupprime", RedisSearchValueSanitizer.sanitizeValue(etatSupprime));
		indexFields.put("etatDesactive", RedisSearchValueSanitizer.sanitizeValue(etatDesactive));
		indexFields.put("nom", RedisSearchValueSanitizer.sanitizeValue(donneesPerso.getNom()));
		indexFields.put("prenom", RedisSearchValueSanitizer.sanitizeValue(donneesPerso.getPrenom()));
		indexFields.put("email", RedisSearchValueSanitizer.sanitizeValue(donneesPerso.getEmail()));
		return indexFields;
	}

	@Override
	public long getTTL() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 15;
		int result = super.hashCode();
		result = prime * result + Objects.hash(donneesPerso, coordsFinancieres, entreprise, contacts, tags, etatSupprime, etatDesactive, listeActivites);
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
		Contact other = (Contact) obj;
		return Objects.equals(donneesPerso, other.donneesPerso) && Objects.equals(entreprise, other.entreprise);
	}

	@Override
	public ConteneurEvenement validate(String parent, Contexte contexte) throws BusinessException {
		ConteneurEvenement conteneur = super.validate(parent, contexte);

		boolean ok = true;

		/* verifications NOT NULL */
		if (donneesPerso == null) {
			conteneur.add(NiveauEvenement.ERROR,
					parent + this.getClass().getCanonicalName() + " : "
							+ "L'objet DonneesPersonnelles ne peut pas etre nul!",
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
			conteneur.addAll(donneesPerso.validate());
			conteneur.addAll(coordsFinancieres.validate());
		}

		conteneur.addAll(entreprise.validate());

		return conteneur;
	}
}