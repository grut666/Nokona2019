package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nokona.data.NokonaDatabaseModel;
import com.nokona.enums.ModelType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.ModelFormatter;
import com.nokona.model.Employee;
import com.nokona.model.Model;
import com.nokona.utilities.DateUtilities;
import com.nokona.validator.ModelValidator;

public class NokonaDAOModel extends NokonaDAO implements NokonaDatabaseModel {
	public NokonaDAOModel() throws DatabaseException {
		super();

	}

	public NokonaDAOModel(String userName, String password) throws DatabaseException {
		super(userName, password);
	}

	PreparedStatement psGetModelByKey;
	PreparedStatement psGetModelByModelId;
	PreparedStatement psGetModels;
	PreparedStatement psAddModel;
	PreparedStatement psAddModelDupeCheck;
	PreparedStatement psUpdateModel;

	PreparedStatement psDelModelByKey;
	PreparedStatement psDelModelByModelId;

	@Override
	public Model getModelByKey(long key) throws DataNotFoundException {
		Model model = null;
		if (psGetModelByKey == null) {
			try {
				psGetModelByKey = conn.prepareStatement("Select * from Model where Model.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetModelByKey.setLong(1, key);
			ResultSet rs = psGetModelByKey.executeQuery();
			if (rs.next()) {
				model = convertModelFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Model key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return model;
	}

	@Override
	public Model getModel(String modelID) throws DataNotFoundException {
		Model model = null;
		if (psGetModelByModelId == null) {
			try {
				psGetModelByModelId = conn.prepareStatement("Select * from Model where ModelID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetModelByModelId.setString(1, modelID);
			ResultSet rs = psGetModelByModelId.executeQuery();
			if (rs.next()) {
				model = convertModelFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Model ModelID " + modelID + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return model;
	}

	@Override
	public List<Model> getModels() {
		List<Model> models = new ArrayList<Model>();
		if (psGetModels == null) {
			try {
				psGetModels = conn.prepareStatement("Select * from Model order by modelId");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetModels.executeQuery();
			while (rs.next()) {
				models.add(convertModelFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return models;
	}

	@Override
	public Model updateModel(Model modelIn) throws DatabaseException {

		if (psUpdateModel == null) {
			try {
				psUpdateModel = conn.prepareStatement(
						"Update Model Set modelId = ?, Description = ?, StandardQuantity = ?, Type = ?, IsDeleted = ?, deleteDate = ? "
								+ "WHERE Model.KEY = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		Model formattedModel = ModelFormatter.format(modelIn);
		String validateMessage = ModelValidator.validateUpdate(formattedModel, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateModel.setString(1, formattedModel.getModelId());
			psUpdateModel.setString(2, formattedModel.getDescription());
			psUpdateModel.setInt(3, formattedModel.getStandardQuantity());
			psUpdateModel.setString(4, formattedModel.getModelType().getModelType());
			psUpdateModel.setString(5, formattedModel.isDeleted() ? "T" : "F");
			psUpdateModel.setDate(6, DateUtilities.convertUtilDateToSQLDate(formattedModel.getDeletedDate()));
			psUpdateModel.setLong(7, formattedModel.getKey());
			int rowCount = psUpdateModel.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			return getModelByKey(formattedModel.getKey());

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}
	

	@Override
	public Model addModel(Model modelIn) throws DatabaseException {
		// Dupe Data Check
		
				if (modelIn == null) {
					throw new NullInputDataException("Model cannot be null");
				}
				Model formattedModel = ModelFormatter.format(modelIn);
				if (psAddModelDupeCheck == null) {
					try {
						psAddModelDupeCheck = conn
								.prepareStatement("Select * from Model where ModelID = ?");

					} catch (SQLException e) {
						System.err.println(e.getMessage());
						throw new DatabaseException(e.getMessage());
					}
				}
				try {
				psAddModelDupeCheck.setString(1, formattedModel.getModelId());
				ResultSet rs = psAddModelDupeCheck.executeQuery();
				if (rs.next()) {
					throw new DuplicateDataException("Model ID is already in use");
				}
				}
				catch(SQLException e) {
					System.err.println(e.getMessage());
					throw new DuplicateDataException(e.getMessage(), e);
				}
				if (psAddModel == null) {
					try {
						psAddModel = conn.prepareStatement(
								"Insert into Model (ModelId, Description, StandardQuantity, Type, IsDeleted, DeleteDate) values (?,?,?,?,?,?)",
								PreparedStatement.RETURN_GENERATED_KEYS);

					} catch (SQLException e) {
						System.err.println(e.getMessage());
						throw new DatabaseException(e.getMessage());
					}
				}
				String validateMessage = ModelValidator.validateAdd(formattedModel, conn);
				if (!"".equals(validateMessage)) {
					throw new DatabaseException(validateMessage);
				}
				try {
					psAddModel.setString(1, formattedModel.getModelId());
					psAddModel.setString(2, formattedModel.getDescription());
					psAddModel.setInt(3, formattedModel.getStandardQuantity());
					psAddModel.setString(4, formattedModel.getModelType().getModelType());
					psAddModel.setString(5, formattedModel.isDeleted() ? "T" : "F");
					psAddModel.setDate(6, DateUtilities.convertUtilDateToSQLDate(formattedModel.getDeletedDate()));
					int rowCount = psAddModel.executeUpdate();

					if (rowCount != 1) {
						throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
					}
					Employee newEmp = new Employee();
					try (ResultSet generatedKeys = psAddModel.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							newEmp.setKey(generatedKeys.getLong(1));
							return getModelByKey(generatedKeys.getLong(1));
						} else {
							throw new SQLException("Creating user failed, no ID obtained.");
						}
					}
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					throw new DuplicateDataException(e.getMessage(), e);
				}
			}


	@Override
	public void deleteModelByKey(long key) throws DatabaseException {
		// TODO Set isdeleted status to false instead?
		
		if (psDelModelByKey == null) {
			try {
				psDelModelByKey = conn.prepareStatement("Delete From Model where Model.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelModelByKey.setLong(1, key);
			int rowCount = psDelModelByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Model key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteModel(String modelID) throws DatabaseException {
		if (modelID == null) {
			throw new NullInputDataException("empID cannot be null");
		}
		if (psDelModelByModelId == null) {
			try {
				psDelModelByModelId = conn.prepareStatement("Delete From Model where ModelID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelModelByModelId.setString(1, modelID);
			int rowCount = psDelModelByModelId.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Emp ID " + modelID + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	private Model convertModelFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String modelId = rs.getString("ModelID");
		String description = rs.getString("Description");
		int standardQuantity = rs.getInt("standardQuantity");
		java.sql.Date sqlDate = rs.getDate("deleteDate");
		Date deleteDate = null;
		if (sqlDate != null) {
			deleteDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("deleteDate"));
		}
		String modelTypeString = rs.getString("Type");
		ModelType modelType = ModelType.UNKNOWN;
		if ("B".equals(modelTypeString) || "S".equals(modelTypeString)) {
			modelType = ModelType.valueOf(modelTypeString);
		}
		boolean deleted = rs.getString("IsDeleted").equals("Y") ? true : false;

		return new Model(modelId, description, standardQuantity, modelType, key, deleted, deleteDate);
	}
}
