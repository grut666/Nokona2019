package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseLevelCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.LevelCodeFormatter;
import com.nokona.model.LevelCode;
import com.nokona.validator.LevelCodeValidator;

public class NokonaDAOLevelCode extends NokonaDAO implements NokonaDatabaseLevelCode {
	

	public NokonaDAOLevelCode() throws DatabaseException {
		super();
	}

	public NokonaDAOLevelCode(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public LevelCode getLevelCodeByKey(long key) throws DataNotFoundException {
		LevelCode levelCode = null;
		try (PreparedStatement psGetLevelCodeByKey = conn
				.prepareStatement("Select * from LevelCode where LevelCode.Key = ?");) {
			psGetLevelCodeByKey.setLong(1, key);
			try (ResultSet rs = psGetLevelCodeByKey.executeQuery()) {
				if (rs.next()) {
					levelCode = convertLevelCodeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("LevelCode key " + key + " is not in DB");
				}
				return levelCode;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}
	}

	@Override
	public LevelCode getLevelCode(int code) throws DataNotFoundException {
		LevelCode levelCode = null;
		try (PreparedStatement psGetLevelCode = conn.prepareStatement("Select * from LevelCode where LevelCode = ?");) {
			psGetLevelCode.setInt(1, code);
			try (ResultSet rs = psGetLevelCode.executeQuery()) {
				if (rs.next()) {
					levelCode = convertLevelCodeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("LevelCode levelCode " + code + " is not in DB");
				}
				return levelCode;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}

	}

	@Override
	public List<LevelCode> getLevelCodes() throws DatabaseException {
		List<LevelCode> levelCodes = new ArrayList<LevelCode>();
		try (PreparedStatement psGetLevelCodes = conn.prepareStatement("Select * from LevelCode order by LevelCode");
				ResultSet rs = psGetLevelCodes.executeQuery()) {
			while (rs.next()) {
				levelCodes.add(convertLevelCodeFromResultSet(rs));
			}
			return levelCodes;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public LevelCode updateLevelCode(LevelCode levelCodeIn) throws DatabaseException {

		try (PreparedStatement psUpdateLevelCode = conn.prepareStatement(
				"Update LevelCode Set LevelCode = ?, LevelRate = ? " + "WHERE LevelCode.Key = ?")) {
			LevelCode formattedLevelCode = LevelCodeFormatter.format(levelCodeIn);
			String validateMessage = LevelCodeValidator.validateUpdate(formattedLevelCode, conn);
			if (!"".equals(validateMessage)) {
				throw new DatabaseException(validateMessage);
			}
			psUpdateLevelCode.setInt(1, formattedLevelCode.getLevelCode());
			psUpdateLevelCode.setDouble(2, formattedLevelCode.getRate());
			psUpdateLevelCode.setLong(3, formattedLevelCode.getKey());
			int rowCount = psUpdateLevelCode.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			return getLevelCodeByKey(formattedLevelCode.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public LevelCode addLevelCode(LevelCode levelCodeIn) throws DatabaseException {
		if (levelCodeIn == null) {
			throw new NullInputDataException("Level Code cannot be null");
		}
		try (PreparedStatement psAddLevelCode = conn.prepareStatement(
				"Insert into LevelCode (LevelCode, LevelRate) values (?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);) {
			LevelCode formattedLevelCode = LevelCodeFormatter.format(levelCodeIn);
			String validateMessage = LevelCodeValidator.validateAdd(levelCodeIn, conn);
			if (!"".equals(validateMessage)) {
				System.out.println(validateMessage);
				throw new DuplicateDataException(validateMessage);
			}
			psAddLevelCode.setInt(1, formattedLevelCode.getLevelCode());
			psAddLevelCode.setDouble(2, formattedLevelCode.getRate());

			int rowCount = psAddLevelCode.executeUpdate();

			if (rowCount != 1) {
				throw new DuplicateDataException("Error.  Inserted " + rowCount + " rows");
			}
			LevelCode newLevelCode = new LevelCode();
			try (ResultSet generatedKeys = psAddLevelCode.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newLevelCode.setKey(generatedKeys.getLong(1));
					// loggit("ADD", newLevelCode);
					return getLevelCodeByKey(generatedKeys.getLong(1));
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
	public void deleteLevelCodeByKey(long key) throws DatabaseException {

		try (PreparedStatement psDelLevelCodeByKey = conn
				.prepareStatement("Delete From LevelCode where LevelCode.Key = ?");) {
			psDelLevelCodeByKey.setLong(1, key);
			int rowCount = psDelLevelCodeByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public void deleteLevelCode(int levelCode) throws DatabaseException {

			try(PreparedStatement psDelLevelCodeByLevelCode = conn.prepareStatement("Delete From LevelCode where LevelCode = ?");) {
				psDelLevelCodeByLevelCode.setInt(1, levelCode);
				int rowCount = psDelLevelCodeByLevelCode.executeUpdate();

				if (rowCount == 0) {
					throw new DataNotFoundException("Error.  Delete Level Code " + levelCode + " failed");
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}		
	}

	private LevelCode convertLevelCodeFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getLong("Key");
		int levelCode = rs.getInt("LevelCode");

		double levelRate = rs.getDouble("LevelRate");

		return new LevelCode(key, levelCode, levelRate);
	}
	
}
