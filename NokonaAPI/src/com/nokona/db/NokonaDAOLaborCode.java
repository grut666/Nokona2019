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
import com.nokona.formatter.LaborCodeFormatter;
import com.nokona.model.LaborCode;
import com.nokona.model.Operation;
import com.nokona.validator.LaborCodeValidator;

public class NokonaDAOLaborCode extends NokonaDAO implements NokonaDatabaseLaborCode {
	public NokonaDAOLaborCode() throws DatabaseException {
		super();	
	}
	public NokonaDAOLaborCode(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	PreparedStatement psGetLaborCode;
	PreparedStatement psGetLaborCodeByKey;
	PreparedStatement psGetLaborCodes;
	PreparedStatement psAddLaborCode;
	PreparedStatement psUpdateLaborCode;

	PreparedStatement psDelLaborCodeByKey;
	PreparedStatement psDelLaborCodeByLaborCode;

	
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
							"WHERE LaborCode.KEY = ?");

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
			psUpdateLaborCode.setInt(1, formattedLaborCode.getCode());
			psUpdateLaborCode.setString(2, formattedLaborCode.getDescription());
			psUpdateLaborCode.setDouble(3, formattedLaborCode.getRate());
			psUpdateLaborCode.setLong(4, formattedLaborCode.getKey());
			int rowCount = psUpdateLaborCode.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			return getLaborCodeByKey(formattedLaborCode.getKey());
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public LaborCode addLaborCode(LaborCode laborCodeIn) throws DatabaseException {
		if (psAddLaborCode == null) {
			try {
				psAddLaborCode = conn.prepareStatement(
						"Insert into LaborCode (LaborCode, Description, LaborRate, Key) values (?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		LaborCode formattedLaborCode = LaborCodeFormatter.format(laborCodeIn);
		String validateMessage = LaborCodeValidator.validateAdd(laborCodeIn, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psAddLaborCode.setInt(1, formattedLaborCode.getCode());
			psAddLaborCode.setString(2, formattedLaborCode.getDescription());
			psAddLaborCode.setDouble(3, formattedLaborCode.getRate());
			psAddLaborCode.setLong(5, formattedLaborCode.getKey());

			int rowCount = psAddLaborCode.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			Operation newOp = new Operation();
			try (ResultSet generatedKeys = psAddLaborCode.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newOp.setKey(generatedKeys.getLong(1));
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
				psDelLaborCodeByKey = conn.prepareStatement("Delete From LaborCode where Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelLaborCodeByKey.setLong(1, key);
			int rowCount = psDelLaborCodeByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}

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

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelLaborCodeByLaborCode.setInt(1, laborCode);
			int rowCount = psDelLaborCodeByLaborCode.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Labor Code " + laborCode + " failed");
			}

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
}
