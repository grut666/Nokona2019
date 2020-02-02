package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Model;
import com.nokona.model.ModelDetail;
import com.nokona.model.ModelHeader;

public interface NokonaDatabaseModel extends NokonaDatabase {

	List<ModelHeader> getModelHeaders() throws DatabaseException;
	ModelHeader getModelHeaderByKey(long key) throws DatabaseException;
	ModelHeader getModelHeader(String modelId) throws DatabaseException;
	ModelHeader updateModelHeader(ModelHeader modelHeader) throws DatabaseException;
	ModelHeader addModelHeader(ModelHeader modelHeader) throws DatabaseException;
	
	ModelDetail getModelDetail(String modelId) throws DatabaseException;
	ModelDetail updateModelDetail(ModelDetail modelDetail)throws DatabaseException;
	ModelDetail addModelDetail(ModelDetail modelDetail) throws DatabaseException;
	
	Model getModel(String modelId) throws DatabaseException;
	Model updateModel(Model model) throws DatabaseException;
	Model addModel(Model model) throws DatabaseException;
	void deleteModelByKey(long key) throws DatabaseException;
	void deleteModel(String modelId) throws DatabaseException;
	
	
}
