package com.nokona.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokona.model.Operation;

// No duplicate empID
// No duplicate barCodeID
public class OperationValidator {
	private static PreparedStatement updatePS;
	private static PreparedStatement addPS;

	
	public static String validateAdd(Operation operationIn, Connection conn) {

		String errors = "";
		if (addPS == null) {
			try {
				addPS = conn.prepareStatement(
						"Select Operation.key from Operation where opCode = ?");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					addPS.setString(1, operationIn.getOpCode());
					ResultSet rs = addPS.executeQuery();
					if (rs.next()) {
						errors += "Add: Bar Code ID or Emp ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}				
			}
		return errors;
	}

	public static String validateUpdate(Operation operationIn, Connection conn) {
		String errors = "";
		if (updatePS == null) {
			try {
				updatePS = conn.prepareStatement(
						"Select Operation.key from Operation where Operation.key <> ? and (OpCode = ?)");
			} catch (SQLException e) {
				errors += e.getMessage() + "\n";
			}	
		} else {
				try {
					updatePS.setLong(1, operationIn.getKey());
					updatePS.setString(2, operationIn.getOpCode());
					ResultSet rs = updatePS.executeQuery();
					if (rs.next()) {
						errors += "Update:Operation ID already in use\n";
					}
					rs.close();
				} catch (SQLException e) {
					errors += e.getMessage() + "\n";
				}
				
			}
		return errors;
			
		}


}
