package com.nokona.data;

import java.sql.Connection;

public interface NokonaDatabase {
	void rollback();
	void commit();
	Connection getConn();
	void setConn(Connection conn);
}
