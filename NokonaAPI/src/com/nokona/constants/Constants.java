package com.nokona.constants;

public class Constants {
	public final static String INTERNAL="internalOAuth";
	public final static String SCOPE="Nokona Super Scope";
	public final static String ADMIN="Admin";
	public final static String EMPLOYEE="Employee";
	public final static String READER="Reader";
	
	// Nokona development
//	 public static String MYSQL_DB_URL = "jdbc:mysql://192.168.1.2:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true";
//	 public final static String PATH_TO_JAVA = "/Program Files/Common Files/Oracle/Java/javapath/java.exe"; // Path where java.exe lives
// 	 public final static String PATH_TO_TRANSFER_JAR = "/codebase/Nokona2019"; // Jar file that runs the command to update Access from MYSQL transfer tables
//	 public final static String ACCESS_DB_URL = "jdbc:ucanaccess://E:Apps/nokona/nokona.mdb";
	
	// Local development
	
	public final static String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC" + 
	"&useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false&autoReconnect=true" +
			"&allowPublicKeyRetrieval=True";
	public final static String PATH_TO_JAVA = "/Program Files/Java/jdk1.8.0_162/bin/java.exe"; // Path where java.exe lives
	public final static String PATH_TO_TRANSFER_JAR = "/codebase/Nokona2019"; // Jar file that runs the command to update Access from MYSQL transfer tables
	public final static String ACCESS_DB_URL = "jdbc:ucanaccess://C:/codebase/Data/noktest.mdb";
	

	
	
	public final static String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	public final static String ACCESS_JDBC_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
//	public final static String USER_NAME="nokona";       // Local development
	public final static String USER_NAME="nokona";       // Local development
	public final static String PASSWORD="xyz1234!";    // Local development
	
}
