package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseLaborCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.LaborCodeFormatter;
import com.nokona.model.LaborCode;
import com.nokona.validator.LaborCodeValidator;

public class NokonaDAOLaborCode extends NokonaDAO implements NokonaDatabaseLaborCode {
	

	public NokonaDAOLaborCode() throws DatabaseException {
		super();
	}

	public NokonaDAOLaborCode(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public LaborCode getLaborCodeByKey(long key) throws DataNotFoundException {
		LaborCode laborCode = null;
		try (PreparedStatement psGetLaborCodeByKey = conn
				.prepareStatement("Select * from LaborCode where LaborCode.Key = ?");) {
			psGetLaborCodeByKey.setLong(1, key);
			try (ResultSet rs = psGetLaborCodeByKey.executeQuery()) {
				if (rs.next()) {
					laborCode = convertLaborCodeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("LaborCode key " + key + " is not in DB");
				}
				return laborCode;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}
	}

	@Override
	public LaborCode getLaborCode(int code) throws DataNotFoundException {
		LaborCode laborCode = null;
		try (PreparedStatement psGetLaborCode = conn.prepareStatement("Select * from LaborCode where LaborCode = ?");) {
			psGetLaborCode.setInt(1, code);
			try (ResultSet rs = psGetLaborCode.executeQuery()) {
				if (rs.next()) {
					laborCode = convertLaborCodeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("LaborCode laborCode " + code + " is not in DB");
				}
				return laborCode;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}

	}

	@Override
	public List<LaborCode> getLaborCodes() throws DatabaseException {
		List<LaborCode> laborCodes = new ArrayList<LaborCode>();
		try (PreparedStatement psGetLaborCodes = conn.prepareStatement("Select * from LaborCode order by LaborCode");
				ResultSet rs = psGetLaborCodes.executeQuery()) {
			while (rs.next()) {
				laborCodes.add(convertLaborCodeFromResultSet(rs));
			}
			return laborCodes;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public LaborCode updateLaborCode(LaborCode laborCodeIn) throws DatabaseException {

		try (PreparedStatement psUpdateLaborCode = conn.prepareStatement(
				"Update LaborCode Set LaborCode = ?, Description = ?, LaborRate = ? " + "WHERE LaborCode.Key = ?")) {
			LaborCode formattedLaborCode = LaborCodeFormatter.format(laborCodeIn);
			String validateMessage = LaborCodeValidator.validateUpdate(formattedLaborCode, conn);
			if (!"".equals(validateMessage)) {
				throw new DatabaseException(validateMessage);
			}
			psUpdateLaborCode.setInt(1, formattedLaborCode.getLaborCode());
			psUpdateLaborCode.setString(2, formattedLaborCode.getDescription());
			psUpdateLaborCode.setDouble(3, formattedLaborCode.getRate());
			psUpdateLaborCode.setLong(4, formattedLaborCode.getKey());
			int rowCount = psUpdateLaborCode.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			return getLaborCodeByKey(formattedLaborCode.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public LaborCode addLaborCode(LaborCode laborCodeIn) throws DatabaseException {
		if (laborCodeIn == null) {
			throw new NullInputDataException("Labor Code cannot be null");
		}
		try (PreparedStatement psAddLaborCode = conn.prepareStatement(
				"Insert into LaborCode (LaborCode, Description, LaborRate) values (?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);) {
			LaborCode formattedLaborCode = LaborCodeFormatter.format(laborCodeIn);
			String validateMessage = LaborCodeValidator.validateAdd(laborCodeIn, conn);
			if (!"".equals(validateMessage)) {
				System.out.println(validateMessage);
				throw new DuplicateDataException(validateMessage);
			}
			psAddLaborCode.setInt(1, formattedLaborCode.getLaborCode());
			psAddLaborCode.setString(2, formattedLaborCode.getDescription());
			psAddLaborCode.setDouble(3, formattedLaborCode.getRate());

			int rowCount = psAddLaborCode.executeUpdate();

			if (rowCount != 1) {
				throw new DuplicateDataException("Error.  Inserted " + rowCount + " rows");
			}
			LaborCode newLaborCode = new LaborCode();
			try (ResultSet generatedKeys = psAddLaborCode.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newLaborCode.setKey(generatedKeys.getLong(1));
					// loggit("ADD", newLaborCode);
					return getLaborCodeByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating operation failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public void deleteLaborCodeByKey(long key) throws DatabaseException {

		try (PreparedStatement psDelLaborCodeByKey = conn
				.prepareStatement("Delete From LaborCode where LaborCode.Key = ?");) {
			psDelLaborCodeByKey.setLong(1, key);
			int rowCount = psDelLaborCodeByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public void deleteLaborCode(int laborCode) throws DatabaseException {

			try(PreparedStatement psDelLaborCodeByLaborCode = conn.prepareStatement("Delete From LaborCode where LaborCode = ?");) {
				psDelLaborCodeByLaborCode.setInt(1, laborCode);
				int rowCount = psDelLaborCodeByLaborCode.executeUpdate();

				if (rowCount == 0) {
					throw new DataNotFoundException("Error.  Delete Labor Code " + laborCode + " failed");
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}		
	}

	private LaborCode convertLaborCodeFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getLong("Key");
		int laborCode = rs.getInt("LaborCode");
		String description = rs.getString("Description");
		double laborRate = rs.getDouble("LaborRate");

		return new LaborCode(key, laborCode, description, laborRate);
	}
	// protected void loggit(String TypeOfUpdate, LaborCode laborCodeIn) throws
	// DatabaseException {
	// flatLog(TypeOfUpdate, laborCodeIn);
	// // This is for moving updates to the Access DB until the application has been
	// // completely ported over
	// if (psTransferLaborCode == null) {
	// try {
	// psTransferLaborCode = conn.prepareStatement(
	// "Insert into Transfer_LaborCode (LaborCode, Description, LaborRate, UDorI,
	// Transfer_LaborCode.Key) values (?,?,?,?,?)");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// try {
	// psTransferLaborCode.setInt(1, laborCodeIn.getLaborCode());
	// psTransferLaborCode.setString(2, laborCodeIn.getDescription());
	// psTransferLaborCode.setDouble(3, laborCodeIn.getRate());
	// psTransferLaborCode.setString(4, TypeOfUpdate);
	// psTransferLaborCode.setLong(5, laborCodeIn.getKey());
	// int rowCount = psTransferLaborCode.executeUpdate();
	// if (rowCount != 1) {
	// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
	// }
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	//
	// }
	//
	// protected void flatLog(String TypeOfUpdate, LaborCode laborCodeIn) {
	// Handler consoleHandler = null;
	// Handler fileHandler = null;
	// try {
	// // Creating consoleHandler and fileHandler
	// consoleHandler = new ConsoleHandler();
	// fileHandler = new FileHandler("/logs/laborcode.log", 0, 1, true);
	//
	// // Assigning handlers to LOGGER object
	// LOGGER.addHandler(consoleHandler);
	// LOGGER.addHandler(fileHandler);
	//
	// // Setting levels to handlers and LOGGER
	// consoleHandler.setLevel(Level.ALL);
	// fileHandler.setLevel(Level.ALL);
	// LOGGER.setLevel(Level.ALL);
	//
	// LOGGER.log(Level.INFO, TypeOfUpdate, gson.toJson(laborCodeIn));
	// fileHandler.close();
	// } catch (IOException exception) {
	// LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
	// }
	// }
}
