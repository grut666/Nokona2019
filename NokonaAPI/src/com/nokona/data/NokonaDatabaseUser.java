package com.nokona.data;

import java.util.List;

import com.nokona.model.User;
import com.nokona.exceptions.DatabaseException;

public interface NokonaDatabaseUser extends NokonaDatabase {

	List<User> getUsers() throws DatabaseException;

	User getUserByKey(long key) throws DatabaseException;

	User getUser(String userID) throws DatabaseException;

	User updateUser(User user) throws DatabaseException;

	User addUser(User user) throws DatabaseException;

	void deleteUserByKey(long key) throws DatabaseException;

	void deleteUser(String userID) throws DatabaseException;
}
