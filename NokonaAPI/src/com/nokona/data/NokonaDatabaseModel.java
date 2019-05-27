package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Model;

public interface NokonaDatabaseModel extends NokonaDatabase {

	List<Model> getModels() throws DatabaseException;

	Model getModelByKey(long key) throws DatabaseException;

	Model getModel(String modelId) throws DatabaseException;

	Model updateModel(Model Model) throws DatabaseException;

	Model addModel(Model Model) throws DatabaseException;

	void deleteModelByKey(long key) throws DatabaseException;

	void deleteModel(String modelId) throws DatabaseException;
}
