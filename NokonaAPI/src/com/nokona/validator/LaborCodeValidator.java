package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.LaborCode;


public class LaborCodeValidator {
	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;

	
	public static String validateAdd(LaborCode laborCodeIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(
						"Select LaborCode.key from LaborCode where LaborCode = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					addPS.setInt(1, laborCodeIn.getCode());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: Labor Code ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}				
			}
		return errors;
	}

	public static String validateUpdate(LaborCode laborCodeIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(
						"Select LaborCode.key from Operation where LaborCode.key <> ? and (LaborCode = ?)");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, laborCodeIn.getKey());
					updatePS.setInt(2, laborCodeIn.getCode());
					ResultSet rs = updatePS.executeQuery();
					if (rs.next()) {
						errors += "Update:LaborCode already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
