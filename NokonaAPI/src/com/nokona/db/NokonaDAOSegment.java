package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nokona.data.NokonaDatabaseSegment;

import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.SegmentFormatter;
import com.nokona.formatter.SegmentHeaderFormatter;
import com.nokona.model.Segment;

import com.nokona.model.SegmentDetail;
import com.nokona.model.SegmentHeader;
import com.nokona.utilities.DateUtilities;

public class NokonaDAOSegment extends NokonaDAO implements NokonaDatabaseSegment {

	private PreparedStatement psGetSegmentHeaderByName;
	private PreparedStatement psGetSegmentHeaders;

	private PreparedStatement psGetSegments;
	private PreparedStatement psGetSegmentByName;

	private PreparedStatement psGetSegmentDetail;
	private PreparedStatement psGetSegmentDetails;

	private PreparedStatement psAddSegmentHeader;
	private PreparedStatement psAddSegmentDetail;
	
	private PreparedStatement psUpdateSegment;
	private PreparedStatement psDelSegmentByName;

	// private PreparedStatement psGetModelSegment;

	public NokonaDAOSegment() throws DatabaseException {
		super();

	}

	public NokonaDAOSegment(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public List<Segment> getSegments() throws DatabaseException {
		List<Segment> segments = new ArrayList<Segment>();
		if (psGetSegments == null) {
			try {
				psGetSegments = getConn().prepareStatement(
						"Select * from Segmentheader SH join Segmentdetail SD on SH.SegmentName = SD.SegmentName "
								+ "order by SH.SegmentName, SD.sequence");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			ResultSet rs = psGetSegments.executeQuery();
			while (rs.next()) {
				segments.add(convertSegmentFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return segments;
	}

	@Override
	public Segment getSegmentByName(String name) throws DatabaseException {
		if (psGetSegmentByName == null) {
			try {
				psGetSegmentByName = getConn().prepareStatement(
						"Select * from SegmentHeader SH join SegmentDetail SD on SH.segmentName = SD.segmentName "
								+ "where SH.segmentName = ? order by SD.sequence");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}

		try {
			psGetSegmentByName.setString(1, name);
			ResultSet rs = psGetSegmentByName.executeQuery();
			if (rs.next()) {
				return convertSegmentFromResultSet(rs);
			}
			throw new DataNotFoundException(name + " segment not found in DB");
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public Segment addSegment(Segment segment) throws DatabaseException {
		if (segment == null) {
			throw new NullInputDataException("Input Segment DTO cannot be null");
		}
		SegmentHeader formattedSegmentHeader = segment.getSegmentHeader();
		formattedSegmentHeader = SegmentHeaderFormatter.format(formattedSegmentHeader);
		try {
			if (psAddSegmentHeader == null) {
				conn.prepareStatement(
						"Insert into SegmentHeader (SegmentName, Description, DateCreated) values (?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);
			}
			if (psGetSegmentDetail == null) {
				conn.prepareStatement("Select * from SegmentDetail where segment = ? order by Sequence");
			}
			if (psAddSegmentDetail == null) {
				conn.prepareStatement("Insert into SegmentDetail (SegmentName, OpCode, Sequence) values (?,?,?)");
			}
		} catch (SQLException ex) {
			throw new DatabaseException("Prepare Statements failed");
		}

		// Should not need any validation so not validating

		try {
			psAddSegmentHeader.setString(1, formattedSegmentHeader.getSegmentName());
			psAddSegmentHeader.setString(2, formattedSegmentHeader.getSegmentDescription());
			psAddSegmentHeader.setDate(3,
					DateUtilities.convertUtilDateToSQLDate(formattedSegmentHeader.getDateCreated()));
			int rowCount = psAddSegmentHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}

		List<SegmentDetail> details = segment.getSegmentDetails();
		try {
			for (SegmentDetail detail : details) {

				psAddSegmentDetail.setString(1, detail.getSegmentName());
				psAddSegmentDetail.setString(2, detail.getOpCode());
				psAddSegmentDetail.setLong(3, detail.getSequence());
				psAddSegmentDetail.addBatch();
			}
			psAddSegmentDetail.executeBatch();
		} catch (SQLException e) {
			throw new InvalidInsertException("Failure on adding segment detail: " + e.getMessage());
		}

		return segment;
	}

	@Override
	public Segment updateSegment(Segment Segment) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Segment deleteSegmentByName(Segment Segment) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SegmentHeader> getSegmentHeaders() throws DatabaseException {
		List<SegmentHeader> segmentHeaders = new ArrayList<SegmentHeader>();
		if (psGetSegmentHeaders == null) {
			try {
				psGetSegmentHeaders = conn.prepareStatement("Select * from SegmentHeader order by segmentName");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			ResultSet rs = psGetSegmentHeaders.executeQuery();
			while (rs.next()) {
				segmentHeaders.add(convertSegmentHeaderFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return segmentHeaders;
	}

	@Override
	public SegmentHeader getSegmentHeaderByName(String segmentName) throws DatabaseException {
		SegmentHeader SegmentHeader = null;
		if (psGetSegmentHeaderByName == null) {
			try {
				psGetSegmentHeaderByName = conn.prepareStatement("Select * from Segmentheader where segmentName = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetSegmentHeaderByName.setString(1, segmentName);
			ResultSet rs = psGetSegmentHeaderByName.executeQuery();
			if (rs.next()) {
				SegmentHeader = convertSegmentHeaderFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Segment key " + segmentName + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return SegmentHeaderFormatter.format(SegmentHeader);
	}

	@Override
	public List<SegmentDetail> getSegmentDetailsByName(String segmentName) throws DatabaseException {
		List<SegmentDetail> segmentDetails = new ArrayList<SegmentDetail>();
		if (psGetSegmentDetails == null) {
			try {
				psGetSegmentDetails = conn
						.prepareStatement("Select * from SegmentDetail where segmentName = ? order by sequence");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetSegmentDetails.setString(1, segmentName);
			ResultSet rs = psGetSegmentDetails.executeQuery();
			while (rs.next()) {
				segmentDetails.add(convertSegmentDetailFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return segmentDetails;
	}

	private Segment convertSegmentFromResultSet(ResultSet rs) throws SQLException {

		SegmentHeader sh = convertSegmentHeaderFromResultSet(rs);
		List<SegmentDetail> details = new ArrayList<SegmentDetail>();
		rs.first(); // reset rs back to first record
		while (rs.next()) {
			details.add(convertSegmentDetailFromResultSet(rs));
		}

		return SegmentFormatter.format(new Segment(sh, details));
	}

	private SegmentHeader convertSegmentHeaderFromResultSet(ResultSet rs) throws SQLException {

		String segmentName = rs.getString("SegmentName");
		String description = rs.getString("Description"); //
		Date dateCreated = DateUtilities.convertSQLDateToUtilDate(rs.getDate("DateCreated"));
		Date dateDeleted = DateUtilities.convertSQLDateToUtilDate(rs.getDate("DateDeleted"));
		boolean isDeleted = "T".equals(rs.getString("IsDeleted")) ? true : false;

		return SegmentHeaderFormatter
				.format(new SegmentHeader(segmentName, description, dateCreated, dateDeleted, isDeleted));
	}

	private SegmentDetail convertSegmentDetailFromResultSet(ResultSet rs) throws SQLException {
		String segmentName = rs.getString("SegmentName");
		String opCode= rs.getString("opCode");
		int sequence = rs.getInt("Sequence");
		SegmentDetail sd = new SegmentDetail(segmentName, opCode, sequence);
		return sd;
	}

}
