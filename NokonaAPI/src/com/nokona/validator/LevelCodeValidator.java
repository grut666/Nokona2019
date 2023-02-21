package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.LevelCode;


public class LevelCodeValidator {
	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;

	
	public static String validateAdd(LevelCode levelCodeIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(
						"Select LevelCode.Key from LevelCode where LevelCode = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
				System.out.println(e);
				
			}	
		} else {
				try {
					addPS.setInt(1, levelCodeIn.getLevelCode());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: Level Code ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}				
			}
		return errors;
	}

	public static String validateUpdate(LevelCode levelCodeIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(
						"Select LevelCode.Key from LevelCode where LevelCode.Key <> ? and (LevelCode = ?)");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, levelCodeIn.getKey());
					updatePS.setInt(2, levelCodeIn.getLevelCode());
					ResultSet rs = updatePS.executeQuery();
					if (rs.next()) {
						errors += "Update:LevelCode already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
