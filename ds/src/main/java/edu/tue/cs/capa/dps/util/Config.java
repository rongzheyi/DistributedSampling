package edu.tue.cs.capa.dps.util;

public class Config
{
	// job configurations
	public static final boolean DEBUG_MODE = true;
	
	// internal private configuration
	public static final String RIGHT_PATH = "dps.right.path";					// used in disc
	public static final String N_SAMPLES = "dps.sample.size";					// used in mappers
	public static final String RIGHT_LINE_LENGTH = "dps.right.line.length";		// used in disc
	public static final String LONGEST_LINE_LENGTH = "dps.longest.line.length";	// used in expander
	
	// internal private separators, used only in disc sampling
	public final static String SepIndexes = ",";
//	public final static String SepIndexRecord = ",";
//	public final static String SepIndexWeight = " ";
//	public final static String SepRecords = ",";
	public final static String SepFilePosition = "@";
		
	
	// can be explicitly set
	public static final String MIN_PATTERN_LENGTH = "dps.min.pattern.length";
	public static final String MAX_RECORD_LENGTH = "dps.max.record.length";
	
	public static final String ITEM_DELIMITER = "dps.item.delimiter";
	
	// default values
	public static final int DEFAULT_MIN_PATTERN_LENGTH = 0; // adjustable
	public static final int DEFAULT_MAX_RECORD_LENGTH = 50; // adjustable
	
	public static final String DEFAULT_ITEM_DELIMITER = " ";


	private Config()
	{

	}
}
