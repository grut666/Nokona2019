package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.LevelCode;

public interface NokonaDatabaseLevelCode extends NokonaDatabase {

	LevelCode getLevelCodeByKey(long key) throws DatabaseException;

	LevelCode getLevelCode(String code) throws DatabaseException;

	LevelCode updateLevelCode(LevelCode laborCode) throws DatabaseException;

	LevelCode addLevelCode(LevelCode laborCode) throws DatabaseException;

	void deleteLevelCodeByKey(long key) throws DatabaseException;

	void deleteLevelCode(String code) throws DatabaseException;

	List<LevelCode> getLevelCodes() throws DatabaseException;
}
