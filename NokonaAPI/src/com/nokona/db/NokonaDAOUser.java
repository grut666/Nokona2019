package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseUser;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.UserFormatter;
import com.nokona.model.User;
import com.nokona.validator.UserValidator;

public class NokonaDAOUser extends NokonaDAO implements NokonaDatabaseUser {
	private PreparedStatement psGetUserByKey;
	private PreparedStatement psGetUserByUserId;
	private PreparedStatement psGetUsers;
	private PreparedStatement psAddUser;
	private PreparedStatement psAddUserDupeCheck;
	private PreparedStatement psUpdateUser;

	private PreparedStatement psMoveDeletedUserByKey;
	private PreparedStatement psMoveDeletedUserByuserId;
	private PreparedStatement psDelUserByKey;
	private PreparedStatement psDelUserByuserId;
	public NokonaDAOUser() throws DatabaseException {
		super();

	}
	public NokonaDAOUser(String userName, String password) throws DatabaseException {
		super(userName, password); 

	}

	@Override
	public List<User> getUsers() throws DatabaseException {
		List<User> Users = new ArrayList<User>();
		if (psGetUsers == null) { 
			try {
				psGetUsers = getConn().prepareStatement("Select * from User order by userID");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			ResultSet rs = psGetUsers.executeQuery();
			while (rs.next()) {
				Users.add(convertUserFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return Users;
	}

	@Override
	public User getUserByKey(long key) throws DatabaseException {
		User user = null;
		if (psGetUserByKey == null) {
			try {
				psGetUserByKey = conn.prepareStatement("Select * from User where User.key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetUserByKey.setLong(1, key);
			ResultSet rs = psGetUserByKey.executeQuery();
			if (rs.next()) {
				user = convertUserFromResultSet(rs);
			} else {
				throw new DataNotFoundException("User key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return UserFormatter.format(user);

	}

	@Override
	public User getUser(String userID) throws DatabaseException, NullInputDataException {
		if (userID == null) {
			throw new NullInputDataException("userID cannot be null");
		}
		User user = null;
		if (psGetUserByUserId == null) {
			try {
				psGetUserByUserId = conn.prepareStatement(
						"Select * from User where User.UserId = ?");


			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e); 
			}
		}
		try {
			psGetUserByUserId.setString(1, userID);
			ResultSet rs = psGetUserByUserId.executeQuery();
			if (rs.next()) {
				user = convertUserFromResultSet(rs);
			} else {
				throw new DataNotFoundException("User userID " + userID + " is not in DB");
			}
		} catch (SQLException e) {

			throw new DatabaseException(e.getMessage(), e);
		}
		return user;
	}

	private User convertUserFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String lName = rs.getString("LastName");
		String fName = rs.getString("FirstName");
		String userId = rs.getString("UserID");
		return UserFormatter.format(new User(key, lName, fName, userId));
	}

	@Override
	public User updateUser(User UserIn) throws DatabaseException {

		if (psUpdateUser == null) {
			try {
				psUpdateUser = conn.prepareStatement(
						"Update User Set LastName = ?, FirstName = ?, UserID = ? "
								+ "WHERE User.KEY = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		User formattedUser = UserFormatter.format(UserIn);
		String validateMessage = UserValidator.validateUpdate(formattedUser, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateUser.setString(1, formattedUser.getLastName());
			psUpdateUser.setString(2, formattedUser.getFirstName());
			psUpdateUser.setString(3, formattedUser.getUserId());
			psUpdateUser.setLong(4, formattedUser.getKey());
			int rowCount = psUpdateUser.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			return getUserByKey(formattedUser.getKey());

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public User addUser(User UserIn) throws DatabaseException {
		// Dupe Data Check
		if (UserIn == null) {
			throw new NullInputDataException("User cannot be null");
		}
		User formattedUser = UserFormatter.format(UserIn);
		if (psAddUserDupeCheck == null) {
			try {
				psAddUserDupeCheck = conn
						.prepareStatement("Select * from User where UserID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
		psAddUserDupeCheck.setString(1, formattedUser.getUserId());
		ResultSet rs = psAddUserDupeCheck.executeQuery();
		if (rs.next()) {
			throw new DuplicateDataException("UserID is already in use");
		}
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
		if (psAddUser == null) {
			try {
				psAddUser = conn.prepareStatement(
						"Insert into User (LastName, FirstName, UserID) values (?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}

		String validateMessage = UserValidator.validateAdd(formattedUser, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psAddUser.setString(1, formattedUser.getLastName());
			psAddUser.setString(2, formattedUser.getFirstName());
			psAddUser.setString(3, formattedUser.getUserId());
			int rowCount = psAddUser.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			User newuser = new User();
			try (ResultSet generatedKeys = psAddUser.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newuser.setKey(generatedKeys.getLong(1));
					return getUserByKey(generatedKeys.getLong(1));
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
	public void deleteUserByKey(long key) throws DatabaseException {

		
		if (psDelUserByKey == null) {
			try {
				psDelUserByKey = conn.prepareStatement("Delete From User where User.Key = ?");
				psMoveDeletedUserByKey = conn.prepareStatement("INSERT INTO Deleted_User (Deleted_User.Key, LastName, FirstName, UserID) " + 
						"  SELECT  User.Key, LastName, FirstName, UserID FROM User WHERE User.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedUserByKey.setLong(1, key);
			int rowCount = psMoveDeletedUserByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Key "+ key + " could not be inserted into delete table");
			}
			psDelUserByKey.setLong(1, key);
			rowCount = psDelUserByKey.executeUpdate();

			if (rowCount == 0) {
				conn.rollback();
				throw new DataNotFoundException("Error.  Delete User key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteUser(String userId) throws DatabaseException {
		
		if (psDelUserByuserId == null) {
			try {
				psDelUserByuserId = conn.prepareStatement("Delete From User where userID = ?");
				psMoveDeletedUserByuserId = conn.prepareStatement("INSERT INTO User (User.Key, LastName, FirstName, UserID) " + 
						"  SELECT User.Key, LastName, FirstName, UserID FROM User WHERE userid = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedUserByuserId.setString(1, userId);
			int rowCount = psMoveDeletedUserByuserId.executeUpdate();
			if (rowCount == 0) {
				throw new InvalidInsertException("userid "+ userId + " could not be inserted into delete table");
			}
			psDelUserByuserId.setString(1,userId);
			rowCount = psDelUserByuserId.executeUpdate();

			if (rowCount == 0) {
				conn.rollback();
				throw new DataNotFoundException("Error.  Delete User userId " + userId + " failed");
			}

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}
}
