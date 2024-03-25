package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.CategoryCode;


public class CategoryCodeValidator {
	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;

	
	public static String validateAdd(CategoryCode categoryCodeIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(
						"Select CategoryCode.Key from CategoryCode where CategoryCode = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
				System.out.println(e);
				
			}	
		} else {
				try {
					addPS.setString(1, categoryCodeIn.getCategoryCode());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: Categort Code ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}				
			}
		return errors;
	}

	public static String validateUpdate(CategoryCode categoryCodeIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(
						"Select CategoryCode.Key from CategoryCode where CategoryCode.Key <> ? and (CategoryCode = ?)");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, categoryCodeIn.getKey());
					updatePS.setString(2, categoryCodeIn.getCategoryCode());
					ResultSet rs = updatePS.executeQuery();
					if (rs.next()) {
						errors += "Update:CategoryCode already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
