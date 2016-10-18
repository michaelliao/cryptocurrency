package com.itranswarp.crytocurrency.store;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.junit.Before;
import org.junit.Test;

public class DatabaseTest {

	Database database;

	@Before
	public void setUp() {
		database = new Database("test.db");
		database.dropTable(TestEntity.class);
		database.createTable(TestEntity.class);
	}

	@Test
	public void testDropAndCreateTable() {
		database.dropTable(TestEntity.class);
		database.createTable(TestEntity.class);
	}

	@Test
	public void testInsert() {
		TestEntity t = newTestEntity("Bob", null, 0);
		database.insert(t);
		assertNotNull(t.id);
	}

	@Test
	public void testInsertWithId() {
		TestEntity t = newTestEntity("Bob", null, 0);
		t.id = "abc-123";
		database.insert(t);
		assertEquals("abc-123", t.id);
	}

	@Test(expected = StoreException.class)
	public void testInsertMissingNonNullValue() {
		TestEntity t = newTestEntity(null, "No.1 Street", 100);
		database.insert(t);
	}

	@Test
	public void testQuery() {
		TestEntity[] ts = new TestEntity[5];
		for (int i = 0; i < ts.length; i++) {
			TestEntity t = newTestEntity("Bob-" + i, "Address-" + i, i * 100);
			t.id = "p-" + i;
			ts[i] = t;
		}
		database.insert(ts);
		// list:
		List<TestEntity> list = database.queryForList(TestEntity.class, "balance <= ?", 200);
		assertEquals(3, list.size());
		// unique:
		TestEntity u = database.queryForUnique(TestEntity.class, "name = ?", "Bob-1");
		assertEquals("p-1", u.id);
		// id:
		TestEntity d = database.queryById(TestEntity.class, "p-3");
		assertEquals("p-3", d.id);
	}

	@Test
	public void testDelete() {
		TestEntity t = newTestEntity("Bob", null, 0);
		database.insert(t);
		TestEntity t2 = new TestEntity();
		t2.id = t.id;
		database.delete(t2);
		assertNull(database.getById(TestEntity.class, t.id));
	}

	@Test
	public void testDeleteBy() {
		TestEntity[] ts = new TestEntity[5];
		for (int i = 0; i < ts.length; i++) {
			TestEntity t = newTestEntity("Bob-" + i, "Address-" + i, i * 100);
			t.id = "p-" + i;
			ts[i] = t;
		}
		database.insert(ts);
		assertEquals(5, database.queryForList(TestEntity.class, "balance >= ?", 0).size());
		database.deleteBy(TestEntity.class, "balance >= ? and balance <= ?", 200, 300);
		assertEquals(3, database.queryForList(TestEntity.class, "balance >= ?", 0).size());
	}

	@Test
	public void testOpenConnection() throws Exception {
		Connection conn = database.openConnection();
		assertNotNull(conn);
		conn.close();
	}

	TestEntity newTestEntity(String name, String address, long balance) {
		TestEntity t = new TestEntity();
		t.name = name;
		t.address = address;
		t.balance = balance;
		return t;
	}
}

@Entity
class TestEntity extends AbstractEntity {

	@Column(length = 100, nullable = false)
	public String name;

	@Column
	public String address;

	public boolean gender;

	@Column(nullable = false)
	public long balance;

	@Transient
	public String title;
}
