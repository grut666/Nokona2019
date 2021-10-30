package com.nokona.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nokona.data.NokonaDatabaseLaborCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.LaborCodeFormatter;
import com.nokona.model.Employee;
import com.nokona.model.LaborCode;
import com.nokona.model.Operation;
import com.nokona.validator.LaborCodeValidator;

public class NokonaDAOLaborCode extends NokonaDAO implements NokonaDatabaseLaborCode {
//	
//	private long key;
//	private int laborCode;
//	private String description;
//	private double rate;
	
	private PreparedStatement psGetLaborCode;
	private PreparedStatement psGetLaborCodeByKey;
	private PreparedStatement psGetLaborCodes;
	private PreparedStatement psAddLaborCode;
	private PreparedStatement psUpdateLaborCode;

	private PreparedStatement psDelLaborCodeByKey;
	private PreparedStatement psDelLaborCodeByLaborCode;
	
	private PreparedStatement psMoveDeletedLaborCodeByLaborCode;
	private PreparedStatement psMoveDeletedLaborCodeByKey;
	
	private PreparedStatement psTransferLaborCode;
	
	private static final Logger LOGGER = Logger.getLogger("LaborCodeLogger");
	
	public NokonaDAOLaborCode() throws DatabaseException {
		super();	
	}
	public NokonaDAOLaborCode(String userName, String password) throws DatabaseException {
		super(userName, password);
		
	}

	@Override
	public LaborCode getLaborCodeByKey(long key) throws DataNotFoundException {
		LaborCode laborCode = null;
		if (psGetLaborCodeByKey == null) {
			try {
				psGetLaborCodeByKey = conn.prepareStatement("Select * from LaborCode where LaborCode.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetLaborCodeByKey.setLong(1, key);
			ResultSet rs = psGetLaborCodeByKey.executeQuery();
			if (rs.next()) {
				laborCode = convertLaborCodeFromResultSet(rs);
			} else {
				throw new DataNotFoundException("LaborCode key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return laborCode;
	}
	@Override
	public LaborCode getLaborCode(int code) throws DataNotFoundException {
		LaborCode laborCode = null;
		if (psGetLaborCode == null) {
			try {
				psGetLaborCode = conn.prepareStatement("Select * from LaborCode where LaborCode = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetLaborCode.setInt(1, code);
			ResultSet rs = psGetLaborCode.executeQuery();
			if (rs.next()) {
				laborCode = convertLaborCodeFromResultSet(rs);
			} else {
				throw new DataNotFoundException("LaborCode laborCode " + code + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return laborCode;
	}

	@Override
	public List<LaborCode> getLaborCodes() {
		List<LaborCode> laborCodes = new ArrayList<LaborCode>(); 
		if (psGetLaborCodes == null) {
			try {
				psGetLaborCodes = conn.prepareStatement("Select * from LaborCode order by LaborCode");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetLaborCodes.executeQuery();
			while (rs.next()) {
				laborCodes.add(convertLaborCodeFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return laborCodes;
	}

	

	@Override
	public LaborCode updateLaborCode(LaborCode laborCodeIn) throws DatabaseException {
		if (psUpdateLaborCode == null) {
			try {
				psUpdateLaborCode = conn.prepareStatement(
						"Update LaborCode Set LaborCode = ?, Description = ?, LaborRate = ? " +
							"WHERE LaborCode.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		LaborCode formattedLaborCode = LaborCodeFormatter.format(laborCodeIn);
		String validateMessage = LaborCodeValidator.validateUpdate(formattedLaborCode, conn);
		if (! "".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateLaborCode.setInt(1, formattedLaborCode.getLaborCode());
			psUpdateLaborCode.setString(2, formattedLaborCode.getDescription());
			psUpdateLaborCode.setDouble(3, formattedLaborCode.getRate());
			psUpdateLaborCode.setLong(4, formattedLaborCode.getKey());
			int rowCount = psUpdateLaborCode.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			// Remove next line when finished with Beta testing
			loggit("UPDATE", laborCodeIn);
			return getLaborCodeByKey(formattedLaborCode.getKey());
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public LaborCode addLaborCode(LaborCode laborCodeIn) throws DatabaseException {
		if (laborCodeIn == null) {
			throw new NullInputDataException("Labor Code cannot be null");
		}
		if (psAddLaborCode == null) {
			try {
				psAddLaborCode = conn.prepareStatement(
						"Insert into LaborCode (LaborCode, Description, LaborRate) values (?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		LaborCode formattedLaborCode = LaborCodeFormatter.format(laborCodeIn);
		String validateMessage = LaborCodeValidator.validateAdd(laborCodeIn, conn);
		if (!"".equals(validateMessage)) {
			System.out.println(validateMessage);
			throw new DuplicateDataException(validateMessage);
		}
		try {
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
					loggit("ADD", newLaborCode);
					return getLaborCodeByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating operation failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteLaborCodeByKey(long key) throws DatabaseException {
		if (psDelLaborCodeByKey == null) {
			try {
				psDelLaborCodeByKey = conn.prepareStatement("Delete From LaborCode where LaborCode.Key = ?");
				psMoveDeletedLaborCodeByKey = conn.prepareStatement("INSERT INTO Deleted_LaborCode (Deleted_LaborCode.Key, Description, LaborCode, LaborRate) " + 
						"  SELECT LaborCode.Key, Description, LaborCode, LaborRate FROM LaborCode WHERE LaborCode.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedLaborCodeByKey.setLong(1, key);
			int rowCount = psMoveDeletedLaborCodeByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("LaborCode key "+ key + " could not be inserted into delete table");
			}
			psDelLaborCodeByKey.setLong(1, key);
			rowCount = psDelLaborCodeByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}
			loggit("DELETE_BY_KEY", new LaborCode(key, 0, null, 0));

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}	
	}

	@Override
	public void deleteLaborCode(int laborCode) throws DatabaseException {
		if (psDelLaborCodeByLaborCode == null) {
			try {
				psDelLaborCodeByLaborCode = conn.prepareStatement("Delete From LaborCode where LaborCode = ?");
				psMoveDeletedLaborCodeByLaborCode = conn.prepareStatement("INSERT INTO Deleted_LaborCode (Deleted_LaborCode.Key, Description, LaborCode, LaborRate) " + 
						"  SELECT LaborCode.Key, Description, LaborCode, LaborRate FROM LaborCode WHERE LaborCode.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedLaborCodeByLaborCode.setInt(1, laborCode);
			int rowCount = psMoveDeletedLaborCodeByLaborCode.executeUpdate();
			if (rowCount == 0) {
				throw new InvalidInsertException("LaborCode LaborCode "+ laborCode + " could not be inserted into delete table");
			}
			psDelLaborCodeByLaborCode.setInt(1, laborCode);
			rowCount = psDelLaborCodeByLaborCode.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Labor Code " + laborCode + " failed");
			}
			loggit("DELETE_BY_ID", new LaborCode(0, 0, null, 0));
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		
		
	}
	private LaborCode convertLaborCodeFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getLong("Key");
		int laborCode = rs.getInt("LaborCode");
		String description = rs.getString("Description");
		double laborRate = rs.getDouble("LaborRate");

		return new LaborCode(key, laborCode, description, laborRate);
	}
	protected void loggit(String TypeOfUpdate, LaborCode laborCodeIn) throws DatabaseException {
		flatLog(TypeOfUpdate, laborCodeIn);
		// This is for moving updates to the Access DB until the application has been
		// completely ported over
		if (psTransferLaborCode == null) {
			try {
				psTransferLaborCode = conn.prepareStatement(
						"Insert into Transfer_LaborCode (LaborCode, Description, LaborRate, UDorI, Transfer_LaborCode.Key) values (?,?,?,?,?)");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psTransferLaborCode.setInt(1, laborCodeIn.getLaborCode());
			psTransferLaborCode.setString(2, laborCodeIn.getDescription());
			psTransferLaborCode.setDouble(3, laborCodeIn.getRate());
			psTransferLaborCode.setString(4, TypeOfUpdate);
			psTransferLaborCode.setLong(5, laborCodeIn.getKey());
			int rowCount = psTransferLaborCode.executeUpdate();
			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	protected void flatLog(String TypeOfUpdate, LaborCode laborCodeIn) {
		Handler consoleHandler = null;
		Handler fileHandler = null;
		try {
			// Creating consoleHandler and fileHandler
			consoleHandler = new ConsoleHandler();
			fileHandler = new FileHandler("/logs/laborcode.log", 0, 1, true);

			// Assigning handlers to LOGGER object
			LOGGER.addHandler(consoleHandler);
			LOGGER.addHandler(fileHandler);

			// Setting levels to handlers and LOGGER
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);

			LOGGER.log(Level.INFO, TypeOfUpdate, gson.toJson(laborCodeIn));
			fileHandler.close();
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
	}
}
