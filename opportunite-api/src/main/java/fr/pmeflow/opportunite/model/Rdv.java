package fr.pmeflow.opportunite.model;

import java.util.Map;

import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import redis.clients.jedis.search.Schema;

public class Rdv  extends AbstractValidatedEntity implements RedisSearchDao {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 12353632L;
	//-----------------Attributs-------------
	private String oid;

	
	
	//--------------- Methodes --------------
	
	@Override
	public String getOid() {
		return oid;
	}

	@Override
	public void setOid(String oid) {
		this.oid = oid;
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
	

}
