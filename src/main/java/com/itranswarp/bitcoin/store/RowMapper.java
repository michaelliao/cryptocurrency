package com.itranswarp.bitcoin.store;

import java.sql.ResultSet;

public interface RowMapper<T> {

	T map(ResultSet rs) throws Exception;
}
