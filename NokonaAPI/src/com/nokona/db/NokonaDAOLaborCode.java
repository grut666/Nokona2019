package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.nokona.data.NokonaDatabaseLaborCode;
import com.nokona.enums.LaborType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.LaborCodeFormatter;
import com.nokona.model.LaborCode;
import com.nokona.validator.LaborCodeValidator;
@ApplicationScoped
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
		try (PreparedStatement psGetLaborCode = conn.prepareStatement("Select * from LaborCode where LaborCode = ?")) {
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
				"Update LaborCode Set LaborCode = ?, Description = ?, LaborRate = ?, LaborType = ? " + "WHERE LaborCode.Key = ?")) {
			LaborCode formattedLaborCode = LaborCodeFormatter.format(laborCodeIn);
			String validateMessage = LaborCodeValidator.validateUpdate(formattedLaborCode, conn);
			if (!"".equals(validateMessage)) {
				throw new DatabaseException(validateMessage);
			}
			psUpdateLaborCode.setInt(1, formattedLaborCode.getLaborCode());
			psUpdateLaborCode.setString(2, formattedLaborCode.getDescription());
			psUpdateLaborCode.setDouble(3, formattedLaborCode.getRate());
			psUpdateLaborCode.setString(4, formattedLaborCode.getLaborType().toString());
			psUpdateLaborCode.setLong(5, formattedLaborCode.getKey());

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
				"Insert into LaborCode (LaborCode, Description, LaborRate, LaborType) values (?,?,?,?)",
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
			psAddLaborCode.setString(4, formattedLaborCode.getLaborType().toString());

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
				throw new DataNotFoundException("Error.  Delete LaborCode Key " + key + " failed");
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
		LaborType laborType = LaborType.valueOf(rs.getString("LaborType"));

		return new LaborCode(key, laborCode, description, laborRate, laborType);
	}
	
	
	
}
