package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.JobHeader;

// No duplicate jobID
public class JobValidator {

	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;


	public static String validateAdd(JobHeader jobIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(

						"Select JobHeader.key from JobHeader where JobID = ?");

			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					addPS.setString(1, jobIn.getJobId());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: Job ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
	}

	public static String validateUpdate(JobHeader jobHeaderIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(

						"Select JobHeader.key from JobHeader where JobHeader.key <> ? and jobID = ?");

			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, jobHeaderIn.getKey());
					updatePS.setString(2, jobHeaderIn.getJobId());
					ResultSet rs = updatePS.executeQuery();
					if (rs.next()) {
						errors += "Update: Job ID would be a duplicate\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
