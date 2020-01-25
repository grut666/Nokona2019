package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.Employee;
import com.nokona.model.Model;

// No duplicate modelID
public class ModelValidator {
	private static PreparedStatement updatePS1;
	private static PreparedStatement updatePS2;
	private static PreparedStatement addPS;


	public static String validateAdd(Model modelIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(
						"Select Model.key from Model where ModelID = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					addPS.setString(1, modelIn.getModelId());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: Model ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
	}

	public static String validateUpdate(Model modelIn, Connection conn) {
		String errors = "";
//
//		if (updatePS1 == null) {
//			try {
//				updatePS1 = conn.prepareStatement(
//						"Select Model.key from Model where Model.key = ?");
//			} catch (SQLException e) {
//				errors += e.getMessage() + "\n";
//			}	
//		} else {
//				try {
//					updatePS1.setLong(1, modelIn.getKey());
//					ResultSet rs = updatePS1.executeQuery();
//					if (! rs.next()) {
//						errors += "Update: Key doesn't exist\n";
//					}
//					rs.close();
//				} catch (SQLException e) {
//					errors += e.getMessage() + "\n";
//				}
//				
//			}
		if (updatePS2 == null) {
			try {
				updatePS2 = conn.prepareStatement(
						"Select Model.key from Model where Model.key <> ? and isDeleted = 'F' and modelID = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS2.setLong(1, modelIn.getKey());
					updatePS2.setString(2, modelIn.getModelId());
					ResultSet rs = updatePS2.executeQuery();
					if (rs.next()) {
						errors += "Update: Model ID would be a duplicate\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
