package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Operation;

public interface NokonaDatabaseOperation  {

	Operation getOperation(long key) throws DatabaseException;
	Operation getOperation(String operationIn) throws DatabaseException;
	Operation updateOperation(Operation operation) throws DatabaseException;
	Operation addOperation(Operation operation) throws DatabaseException;
	void deleteOperation(long key) throws DatabaseException;
	void deleteOperation(String opID) throws DatabaseException;
	List<Operation> getOperations() throws DatabaseException;	
}
