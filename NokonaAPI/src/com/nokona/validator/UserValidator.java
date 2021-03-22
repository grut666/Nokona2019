package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.User;


public class UserValidator {
	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;


	public static String validateAdd(User userIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(
						"Select User.key from User where UserID = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					addPS.setString(1, userIn.getUserId());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: User ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
	}

	public static String validateUpdate(User userIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(
						"Select User.key from User where User.key <> ? and active = 1 and UserID = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, userIn.getKey());
					updatePS.setString(2, userIn.getUserId());
					ResultSet rs = updatePS.executeQuery();
					if (rs.next()) {
						errors += "Update:User ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
