package fr.pmeflow.opportunite.model;

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
import fr.pmeflow.commons.PmeflowConstant;
import redis.clients.jedis.search.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Opportunite extends AbstractValidatedEntity implements RedisSearchDao {

	// ----------- Attributs -----------
	private static final long serialVersionUID = 1942131605554389593L;

	private String oid;

	@Override
	public String getOid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOid(String oid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getIndexSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getIndexFieldValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// ----------- Methodes -----------

}