package com.nokona.data;

import java.util.List;


import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Segment;
import com.nokona.model.SegmentDetail;
import com.nokona.model.SegmentHeader;

public interface NokonaDatabaseSegment extends NokonaDatabase {
	
	List<Segment> getSegments() throws DatabaseException;
	Segment getSegmentByName(String name) throws DatabaseException;
	Segment addSegment(Segment segment)  throws DatabaseException;
	Segment updateSegment (Segment segment) throws DatabaseException;

	Segment deleteSegmentByName (Segment segment) throws DatabaseException;
	
	List<SegmentHeader> getSegmentHeaders() throws DatabaseException;
	SegmentHeader getSegmentHeaderByName(String segmentName) throws DatabaseException;
	
	List<SegmentDetail> getSegmentDetailsByName(String segmentName) throws DatabaseException;

}
