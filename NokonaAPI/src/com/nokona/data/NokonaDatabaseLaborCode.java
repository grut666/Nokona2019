package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.LaborCode;

public interface NokonaDatabaseLaborCode  {

	LaborCode getLaborCodeByKey(long key) throws DatabaseException;
	LaborCode getLaborCode(int code) throws DatabaseException;
	LaborCode updateLaborCode(LaborCode laborCode) throws DatabaseException;
	LaborCode addLaborCode(LaborCode laborCode) throws DatabaseException;
	void deleteLaborCodeByKey(long key) throws DatabaseException;
	void deleteLaborCode(int code) throws DatabaseException;
	List<LaborCode> getLaborCodes() throws DatabaseException;	
}
