package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.ModelHeader;

// No duplicate modelID
public class ModelValidator {

	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;


	public static String validateAdd(ModelHeader modelIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(

						"Select Model.key from ModelHeader where ModelID = ?");

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

	public static String validateUpdate(ModelHeader modelHeaderIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(

						"Select ModelHeader.key from ModelHeader where ModelHeader.key <> ? and modelID = ?");

			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, modelHeaderIn.getKey());
					updatePS.setString(2, modelHeaderIn.getModelId());
					ResultSet rs = updatePS.executeQuery();
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
