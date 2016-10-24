package com.itranswarp.crytocurrency.store;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Database {

	private final String jdbcConnectionUrl;

	public Database(String dbFile) {
		this.jdbcConnectionUrl = "jdbc:hsqldb:file:" + dbFile;
	}

	public <T extends AbstractEntity> T getById(Class<T> entityClass, String id) {
		List<T> list = queryForList(entityClass, "id = ?", id);
		if (list.isEmpty()) {
			return null;
		}
		if (list.size() > 1) {
			throw new StoreException("More than 1 results.");
		}
		return list.get(0);
	}

	public <T extends AbstractEntity> T queryById(Class<T> entityClass, String id) {
		return queryForUnique(entityClass, "id = ?", id);
	}

	public <T extends AbstractEntity> T queryForUnique(Class<T> entityClass, String where, Object... params) {
		List<T> list = queryForList(entityClass, where, params);
		if (list.isEmpty()) {
			throw new StoreException("Empty result set.");
		}
		if (list.size() > 1) {
			throw new StoreException("More than 1 results.");
		}
		return list.get(0);
	}

	public <T extends AbstractEntity> List<T> queryForList(Class<T> entityClass, String where, Object... params) {
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			EntityInfo ei = getEntityInfo(entityClass);
			return ctx.query(ei, "SELECT * FROM " + ei.table + " WHERE " + where, params);
		} catch (Exception e) {
			throw new StoreException(e);
		}
	}

	/**
	 * Insert entities. Id will be set automatically if the entity's id is not
	 * set.
	 * 
	 * @param entities
	 *            The entities.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> void insert(T... entities) {
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			for (T entity : entities) {
				EntityInfo ei = getEntityInfo(entity.getClass());
				ctx.executeUpdate(ei.insertSQL, ei.getInsertParams(entity));
			}
		} catch (Exception e) {
			throw new StoreException(e);
		}
	}

	/**
	 * Delete entities by ids.
	 * 
	 * @param clazz
	 *            The entity class.
	 * @param ids
	 *            The id list.
	 */
	public <T extends AbstractEntity> void delete(Class<T> clazz, String... ids) {
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			for (String id : ids) {
				EntityInfo ei = getEntityInfo(clazz);
				ctx.executeUpdate(ei.deleteSQL, id);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delete entities. The entity beans only need valid id value.
	 * 
	 * @param entities
	 *            The entities.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> void delete(T... entities) {
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			for (T entity : entities) {
				EntityInfo ei = getEntityInfo(entity.getClass());
				ctx.executeUpdate(ei.deleteSQL, entity.getId());
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends AbstractEntity> void dropTable(Class<T> entityClass) {
		EntityInfo ei = getEntityInfo(entityClass);
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			ctx.executeUpdate("DROP TABLE " + ei.table + " IF EXISTS");
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends AbstractEntity> void createTable(Class<T> entityClass) {
		EntityInfo ei = getEntityInfo(entityClass);
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			ctx.executeUpdate(ei.ddl);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Execute "delete from ... where ...".
	 * 
	 * @param clazz
	 *            Entity class.
	 * @param where
	 *            Where clause, e.g. "blockHash=?"
	 * @param params
	 *            Parameters as varargs.
	 * @return Number of deleted records.
	 */
	public <T extends AbstractEntity> int deleteBy(Class<T> clazz, String where, Object... params) {
		try (ConnectionContext ctx = new ConnectionContext(openConnection())) {
			EntityInfo ei = getEntityInfo(clazz);
			String sql = "DELETE FROM " + ei.table + " WHERE " + where;
			return ctx.executeUpdate(sql, params);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	Connection openConnection() {
		try {
			return DriverManager.getConnection(jdbcConnectionUrl, "SA", "");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	<T extends AbstractEntity> EntityInfo getEntityInfo(Class<T> clazz) {
		String key = clazz.getName();
		EntityInfo ei = entityCache.get(key);
		if (ei == null) {
			ei = new EntityInfo(clazz);
			entityCache.put(key, ei);
		}
		return ei;
	}

	Map<String, EntityInfo> entityCache = new HashMap<String, EntityInfo>();

}

class EntityInfo implements RowMapper {

	final Class<?> clazz;
	final String table;
	final List<Property> properties;
	final String insertSQL;
	final String deleteSQL;
	final String ddl;

	EntityInfo(Class<?> clazz) {
		this.clazz = clazz;
		this.table = getTableName(clazz);
		this.properties = Arrays.asList(clazz.getFields()).stream().map((f) -> {
			if (f.isAnnotationPresent(Transient.class)) {
				return null;
			}
			return new Property(f);
		}).filter((p) -> {
			return p != null;
		}).collect(Collectors.toList());
		this.insertSQL = "INSERT INTO " + this.table + " (" + namesOf(this.properties) + ") VALUES ("
				+ numOf(this.properties.size()) + ")";
		this.deleteSQL = "DELETE FROM " + this.table + " WHERE id = ?";
		this.ddl = "CREATE TABLE " + this.table + " (" + String.join(", ", this.properties.stream().map((p) -> {
			return p.ddl;
		}).collect(Collectors.toList())) + ")";
	}

	@SuppressWarnings("unchecked")
	public <T> T map(ResultSet rs) throws Exception {
		Object o = clazz.newInstance();
		for (Property p : this.properties) {
			String name = p.field.getName();
			Object value = rs.getObject(name);
			p.field.set(o, value);
		}
		return (T) o;
	}

	<T extends AbstractEntity> Object[] getInsertParams(T entity) throws Exception {
		Object[] params = new Object[this.properties.size()];
		int n = 0;
		for (Property prop : this.properties) {
			params[n] = prop.getValue(entity);
			n++;
		}
		return params;
	}

	private String getTableName(Class<?> clazz) {
		Table t = clazz.getAnnotation(Table.class);
		if (t != null && !t.name().isEmpty()) {
			return t.name();
		}
		return clazz.getSimpleName();
	}

	String namesOf(List<Property> list) {
		return String.join(", ", list.stream().map((p) -> {
			return p.name;
		}).collect(Collectors.toList()));
	}

	String numOf(int n) {
		return String.join(", ", Arrays.asList(new String[n]).stream().map((x) -> {
			return "?";
		}).collect(Collectors.toList()));
	}

}

class Property {

	final boolean isId;
	final String name;
	final Field field;
	final String ddl;

	Property(Field field) {
		this.field = field;
		this.name = field.getName();
		this.isId = "id".equals(name);
		final Class<?> type = this.field.getType();
		String ddl = DATA_TYPES.get(type);
		if (ddl == null) {
			throw new IllegalArgumentException("Cannot find SQL type for Java type: " + type.getName());
		}
		ddl = this.name + " " + ddl;
		int length = 255;
		Column column = this.field.getAnnotation(Column.class);
		if (column != null) {
			length = column.length();
			if (!column.columnDefinition().isEmpty()) {
				ddl = column.columnDefinition();
			}
		}
		if (String.class.equals(type)) {
			ddl = ddl + "(" + length + ")";
		}
		boolean nullable = !isId && (column == null || column.nullable());
		ddl = ddl + (nullable ? " NULL" : " NOT NULL");
		ddl = ddl + (isId ? " PRIMARY KEY" : "");
		this.ddl = ddl;
	}

	Object getValue(Object bean) throws Exception {
		return field.get(bean);
	}

	void setValue(Object bean, Object value) throws Exception {
		field.set(bean, value);
	}

	static final Map<Class<?>, String> DATA_TYPES = new HashMap<Class<?>, String>() {
		{
			put(String.class, "VARCHAR");
			put(int.class, "INT");
			put(Integer.class, "INT");
			put(long.class, "BIGINT");
			put(Long.class, "BIGINT");
			put(boolean.class, "BOOLEAN");
			put(Boolean.class, "BOOLEAN");
		}
	};
}

class ConnectionContext implements AutoCloseable {

	final static Log log = LogFactory.getLog(ConnectionContext.class);
	final static ThreadLocal<ConnectionResource> resource = new ThreadLocal<ConnectionResource>();

	public ConnectionContext(Connection conn) {
		ConnectionResource res = resource.get();
		if (res == null) {
			resource.set(new ConnectionResource(conn));
		} else {
			res.count++;
		}
	}

	static Connection getConnection() {
		return resource.get().connection;
	}

	<T> List<T> query(RowMapper mapper, String sql, Object... params) throws Exception {
		log.warn("SQL: " + sql);
		try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
			if (params.length > 0) {
				int n = 1;
				for (Object param : params) {
					ps.setObject(n, param);
					n++;
				}
			}
			List<T> list = new ArrayList<>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapper.map(rs));
				}
			}
			return list;
		}
	}

	int executeUpdate(String sql, Object... params) throws SQLException {
		log.warn("SQL: " + sql);
		try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
			if (params.length > 0) {
				int n = 1;
				for (Object param : params) {
					ps.setObject(n, param);
					n++;
				}
			}
			return ps.executeUpdate();
		}
	}

	@Override
	public void close() {
		ConnectionResource res = resource.get();
		res.count--;
		if (res.count == 0) {
			resource.remove();
			try {
				res.connection.commit();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					res.connection.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

}

class ConnectionResource {

	final Connection connection;
	int count;

	ConnectionResource(Connection connection) {
		this.connection = connection;
		this.count = 0;
		try {
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
}
