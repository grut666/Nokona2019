package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.nokona.data.NokonaDatabaseCategoryCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.CategoryCodeFormatter;
import com.nokona.model.CategoryCode;
import com.nokona.validator.CategoryCodeValidator;
@ApplicationScoped
public class NokonaDAOCategoryCode extends NokonaDAO implements NokonaDatabaseCategoryCode {
	

	public NokonaDAOCategoryCode() throws DatabaseException {
		super();
	}

	public NokonaDAOCategoryCode(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public CategoryCode getCategoryCodeByKey(long key) throws DataNotFoundException {
		CategoryCode categoryCode = null;
		try (PreparedStatement psGetCategoryCodeByKey = conn
				.prepareStatement("Select * from CategoryCode where CategoryCode.Key = ?");) {
			psGetCategoryCodeByKey.setLong(1, key);
			try (ResultSet rs = psGetCategoryCodeByKey.executeQuery()) {
				if (rs.next()) {
					categoryCode = convertCategoryCodeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("CategoryCode key " + key + " is not in DB");
				}
				return categoryCode;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}
	}

	@Override
	public CategoryCode getCategoryCode(String code) throws DataNotFoundException {
		CategoryCode categoryCode = null;
		try (PreparedStatement psGetCategoryCode = conn.prepareStatement("Select * from CategoryCode where CategoryCode = ?");) {
			psGetCategoryCode.setString(1, code);
			try (ResultSet rs = psGetCategoryCode.executeQuery()) {
				if (rs.next()) {
					categoryCode = convertCategoryCodeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("CategoryCode categoryCode " + code + " is not in DB");
				}
				return categoryCode;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}

	}

	@Override
	public List<CategoryCode> getCategoryCodes() throws DatabaseException {
		List<CategoryCode> categoryCodes = new ArrayList<CategoryCode>();
		try (PreparedStatement psGetCategoryCodes = conn.prepareStatement("Select * from CategoryCode order by CategoryCode");
				ResultSet rs = psGetCategoryCodes.executeQuery()) {
			while (rs.next()) {
				categoryCodes.add(convertCategoryCodeFromResultSet(rs));
			}
			return categoryCodes;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public CategoryCode updateCategoryCode(CategoryCode categoryCodeIn) throws DatabaseException {

		try (PreparedStatement psUpdateCategoryCode = conn.prepareStatement(
				"Update CategoryCode Set CategoryCode = ?, Description = ? " + "WHERE CategoryCode.Key = ?")) {
			CategoryCode formattedCategoryCode = CategoryCodeFormatter.format(categoryCodeIn);
			String validateMessage = CategoryCodeValidator.validateUpdate(formattedCategoryCode, conn);
			if (!"".equals(validateMessage)) {
				throw new DatabaseException(validateMessage);
			}
			psUpdateCategoryCode.setString(1, formattedCategoryCode.getCategoryCode());
			psUpdateCategoryCode.setString(2, formattedCategoryCode.getDescription());
			psUpdateCategoryCode.setLong(3, formattedCategoryCode.getKey());
			int rowCount = psUpdateCategoryCode.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			return getCategoryCodeByKey(formattedCategoryCode.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public CategoryCode addCategoryCode(CategoryCode categoryCodeIn) throws DatabaseException {
		if (categoryCodeIn == null) {
			throw new NullInputDataException("Category Code cannot be null");
		}
		try (PreparedStatement psAddCategoryCode = conn.prepareStatement(
				"Insert into CategoryCode (CategoryCode, Description) values (?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);) {
			CategoryCode formattedCategoryCode = CategoryCodeFormatter.format(categoryCodeIn);
			String validateMessage = CategoryCodeValidator.validateAdd(categoryCodeIn, conn);
			if (!"".equals(validateMessage)) {
				System.out.println(validateMessage);
				throw new DuplicateDataException(validateMessage);
			}
			psAddCategoryCode.setString(1, formattedCategoryCode.getCategoryCode());
			psAddCategoryCode.setString(2, formattedCategoryCode.getDescription());

			int rowCount = psAddCategoryCode.executeUpdate();

			if (rowCount != 1) {
				throw new DuplicateDataException("Error.  Inserted " + rowCount + " rows");
			}
			CategoryCode newCategoryCode = new CategoryCode();
			try (ResultSet generatedKeys = psAddCategoryCode.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newCategoryCode.setKey(generatedKeys.getLong(1));
					return getCategoryCodeByKey(generatedKeys.getLong(1));
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
	public void deleteCategoryCodeByKey(long key) throws DatabaseException {

		try (PreparedStatement psDelCategoryCodeByKey = conn
				.prepareStatement("Delete From CategoryCode where CategoryCode.Key = ?");) {
			psDelCategoryCodeByKey.setLong(1, key);
			int rowCount = psDelCategoryCodeByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Category Key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public void deleteCategoryCode(String categoryCode) throws DatabaseException {

			try(PreparedStatement psDelCategoryCodeByCategoryCode = conn.prepareStatement("Delete From CategoryCode where CategoryCode = ?");) {
				psDelCategoryCodeByCategoryCode.setString(1, categoryCode);
				int rowCount = psDelCategoryCodeByCategoryCode.executeUpdate();

				if (rowCount == 0) {
					throw new DataNotFoundException("Error.  Delete Category Code " + categoryCode + " failed");
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}		
	}

	private CategoryCode convertCategoryCodeFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getLong("Key");
		String categoryCode = rs.getString("CategoryCode");

		String categoryDescription = rs.getString("Description");

		return new CategoryCode(key, categoryCode, categoryDescription);
	}
	
}
