package com.nokona.data;

import java.sql.Connection;

import com.nokona.exceptions.DatabaseConnectionException;

public interface NokonaDatabase {
	void rollback();
	void commit();
	Connection getConn();
	void setConn(Connection conn);
}
