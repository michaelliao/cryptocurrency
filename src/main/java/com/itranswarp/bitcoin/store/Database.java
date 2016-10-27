package com.itranswarp.bitcoin.store;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Database {

	static String jdbcConnectionUrl;

	public static Database init(String dbFile) {
		jdbcConnectionUrl = "jdbc:hsqldb:file:" + dbFile;
		try (ConnectionContext ctx = new ConnectionContext()) {
			// this.createTable(BlockEntity.class);
			try (ConnectionContext ctx2 = new ConnectionContext()) {
			}
		}
		try (ConnectionContext ctx = new ConnectionContext()) {
			DatabaseMetaData meta = ConnectionContext.getConnection().getMetaData();
			ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next()) {
				String name = rs.getString(3);
				System.out.println("> " + name);
			}
		} catch (Exception e) {
			throw new StoreException(e);
		}
		return new Database();
	}

	/**
	 * Query by id, or throw exception if not found.
	 */
	public <T extends AbstractEntity> T queryById(Class<T> entityClass, String id) {
		T t = getById(entityClass, id);
		if (t == null) {
			throw new StoreException("Entity not found.");
		}
		return t;
	}

	/**
	 * Get by id, or null if not found.
	 */
	public <T extends AbstractEntity> T getById(Class<T> entityClass, String id) {
		try (ConnectionContext ctx = new ConnectionContext()) {
			EntityInfo<T> ei = getEntityInfo(entityClass);
			List<T> list = ctx.query(ei, "SELECT * FROM " + ei.table + " WHERE " + ei.id.name + " = ?", id);
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
		} catch (Exception e) {
			throw new StoreException(e);
		}
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
		try (ConnectionContext ctx = new ConnectionContext()) {
			EntityInfo<T> ei = getEntityInfo(entityClass);
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
		try (ConnectionContext ctx = new ConnectionContext()) {
			for (T entity : entities) {
				EntityInfo<T> ei = (EntityInfo<T>) getEntityInfo(entity.getClass());
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
		try (ConnectionContext ctx = new ConnectionContext()) {
			for (String id : ids) {
				EntityInfo<T> ei = getEntityInfo(clazz);
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
		try (ConnectionContext ctx = new ConnectionContext()) {
			for (T entity : entities) {
				EntityInfo<T> ei = (EntityInfo<T>) getEntityInfo(entity.getClass());
				ctx.executeUpdate(ei.deleteSQL, entity.getId());
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends AbstractEntity> void dropTable(Class<T> entityClass) {
		EntityInfo<T> ei = getEntityInfo(entityClass);
		try (ConnectionContext ctx = new ConnectionContext()) {
			ctx.executeUpdate("DROP TABLE " + ei.table + " IF EXISTS");
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends AbstractEntity> void createTable(Class<T> entityClass) {
		EntityInfo<T> ei = getEntityInfo(entityClass);
		try (ConnectionContext ctx = new ConnectionContext()) {
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
		try (ConnectionContext ctx = new ConnectionContext()) {
			EntityInfo<T> ei = getEntityInfo(clazz);
			String sql = "DELETE FROM " + ei.table + " WHERE " + where;
			return ctx.executeUpdate(sql, params);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static Connection openConnection() {
		try {
			return DriverManager.getConnection(jdbcConnectionUrl, "SA", "");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	<T extends AbstractEntity> EntityInfo<T> getEntityInfo(Class<T> clazz) {
		String key = clazz.getName();
		EntityInfo<T> ei = (EntityInfo<T>) entityCache.get(key);
		if (ei == null) {
			ei = new EntityInfo<T>(clazz);
			entityCache.put(key, ei);
		}
		return ei;
	}

	Map<String, EntityInfo<?>> entityCache = new HashMap<String, EntityInfo<?>>();

}

class EntityInfo<T extends AbstractEntity> implements RowMapper<T> {

	final Class<T> clazz;
	final String table;
	final List<Property> properties;
	final Property id;
	final String insertSQL;
	final String deleteSQL;
	final String ddl;

	EntityInfo(Class<T> clazz) {
		this.clazz = clazz;
		this.table = getTableName(clazz);
		this.properties = Arrays.stream(clazz.getFields()).map((f) -> {
			if (f.isAnnotationPresent(Transient.class)) {
				return null;
			}
			return new Property(f);
		}).filter((p) -> {
			return p != null;
		}).collect(Collectors.toList());
		Property[] ids = Arrays.stream(clazz.getFields()).filter((f) -> {
			return f.isAnnotationPresent(Id.class);
		}).map((f) -> {
			return new Property(f);
		}).toArray(Property[]::new);
		if (ids.length == 0) {
			throw new IllegalArgumentException("@Id not found.");
		}
		if (ids.length > 1) {
			throw new IllegalArgumentException("Multiple @Id found.");
		}
		this.id = ids[0];
		this.insertSQL = "INSERT INTO " + this.table + " (" + namesOf(this.properties) + ") VALUES ("
				+ numOf(this.properties.size()) + ")";
		this.deleteSQL = "DELETE FROM " + this.table + " WHERE " + this.id.name + " = ?";
		this.ddl = "CREATE TABLE " + this.table + " (" + String.join(", ", this.properties.stream().map((p) -> {
			return p.ddl;
		}).collect(Collectors.toList())) + ")";
	}

	@SuppressWarnings("unchecked")
	public T map(ResultSet rs) throws Exception {
		Object o = clazz.newInstance();
		for (Property p : this.properties) {
			String name = p.field.getName();
			Object value = rs.getObject(name);
			p.field.set(o, value);
		}
		return (T) o;
	}

	Object[] getInsertParams(T entity) throws Exception {
		Object[] params = new Object[this.properties.size()];
		int n = 0;
		for (Property prop : this.properties) {
			params[n] = prop.getValue(entity);
			n++;
		}
		return params;
	}

	private String getTableName(Class<T> clazz) {
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

class ConnectionContext implements AutoCloseable {

	static final Log log = LogFactory.getLog(ConnectionContext.class);
	static final ThreadLocal<ConnectionResource> resource = new ThreadLocal<ConnectionResource>();

	public ConnectionContext() {
		ConnectionResource res = resource.get();
		if (res == null) {
			resource.set(new ConnectionResource(Database.openConnection()));
		} else {
			res.count++;
		}
	}

	static Connection getConnection() {
		return resource.get().connection;
	}

	<T> List<T> query(RowMapper<T> mapper, String sql, Object... params) throws Exception {
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
		this.count = 1;
		try {
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
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
