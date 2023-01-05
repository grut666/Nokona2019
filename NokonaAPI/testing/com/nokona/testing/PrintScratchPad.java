package com.nokona.testing;

import java.net.URL;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.nokona.enums.JobType;
import com.nokona.enums.ReportCategory;
import com.nokona.utilities.BarCodeUtilities;

public class PrintScratchPad {

	public static void main(String[] args) {
//You almost certainly have an older version of Apache POI on your classpath. Quite a few runtimes and other packages will ship older version of Apache POI, so this is an easy problem to hit without your realising. Some will ship just one old jar, some may ship a full set of old POI jars.

//The best way to identify the offending earlier jar files is with a few lines of java. These will load a Core POI class, an OOXML class and a Scratchpad class, and report where they all came from.

//ClassLoader classloader =
//   org.apache.poi.poifs.filesystem.POIFSFileSystem.class.getClassLoader();
//URL res = classloader.getResource(
//             "org/apache/poi/poifs/filesystem/POIFSFileSystem.class");
//String path = res.getPath();
//System.out.println("POI Core came from " + path);

//classloader = org.apache.poi.ooxml.POIXMLDocument.class.getClassLoader();
//res = classloader.getResource("org/apache/poi/ooxml/POIXMLDocument.class");
//path = res.getPath();
//System.out.println("POI OOXML came from " + path);

//classloader = org.apache.poi.hslf.usermodel.HSLFSlideShow.class.getClassLoader();
//res = classloader.getResource("org/apache/poi/hslf/usermodel/HSLFSlideShow.class");
//path = res.getPath();
//System.out.println("POI Scratchpad came from " + path);

}
}
