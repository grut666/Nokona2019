package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.CategoryCode;

public interface NokonaDatabaseCategoryCode extends NokonaDatabase {

	CategoryCode getCategoryCodeByKey(long key) throws DatabaseException;

	CategoryCode getCategoryCode(String code) throws DatabaseException;

	CategoryCode updateCategoryCode(CategoryCode categoryCode) throws DatabaseException;

	CategoryCode addCategoryCode(CategoryCode categoryCode) throws DatabaseException;

	void deleteCategoryCodeByKey(long key) throws DatabaseException;

	void deleteCategoryCode(String code) throws DatabaseException;

	List<CategoryCode> getCategoryCodes() throws DatabaseException;
}
