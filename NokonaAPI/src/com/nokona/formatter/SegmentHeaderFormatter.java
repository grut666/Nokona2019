package com.nokona.formatter;

import com.nokona.model.SegmentHeader;

public class SegmentHeaderFormatter {
	
	public static SegmentHeader format(SegmentHeader segmentHeader) {
		segmentHeader.setSegmentName(formatSegmentName(segmentHeader.getSegmentName()));
		return segmentHeader;
	}

	public static String formatSegmentName(String segmentName) {
		return segmentName.trim().toUpperCase();
	}
	

}
