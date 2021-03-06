package edu.tue.cs.capa.dps.disc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.StringUtils;

import com.google.common.collect.Sets;

import edu.tue.cs.capa.dps.util.Config;
import edu.tue.cs.capa.dps.util.DpsExceptions.MissingParameterException;
import edu.tue.cs.capa.dps.util.Helper;



public class DiscReducer extends MapReduceBase implements Reducer<DoubleWritable, Text, DoubleWritable, Text>
{

	private FileSystem fs;
	private int nSamples;
	private int collectedSample = 0;

	private int minPatternLength;	// XXX: minimum pattern length cannot be set here
	private double maxKey = 0.0d;

	private String delimiter;


	@Override
	public void configure(JobConf jobConf)
	{
		nSamples = jobConf.getInt(Config.N_SAMPLES, 0);
		if (nSamples == 0)
			throw new MissingParameterException("The sample size is not set");

		minPatternLength = jobConf.getInt(Config.MIN_PATTERN_LENGTH, Config.DEFAULT_MIN_PATTERN_LENGTH);

		delimiter = jobConf.get(Config.ITEM_DELIMITER, Config.DEFAULT_ITEM_DELIMITER);
		System.out.println("Item delimiter: " + delimiter);
		
		try
		{
			fs = FileSystem.get(jobConf);
		}
		catch (IOException e)
		{
			System.err.println("IO Exception when reading input file");
			throw new RuntimeException(e);
		}
	}


	@Override
	public void reduce(DoubleWritable key, Iterator<Text> values, OutputCollector<DoubleWritable, Text> output,
					Reporter reporter) throws IOException
	{
		if (maxKey == 0.0d)
		{
			maxKey = key.get();
			System.out.println("The maximum key: " + maxKey);
		}

		while (collectedSample < nSamples && values.hasNext())
		{
			collectedSample++;
			Text value = values.next();
			
			String[] indices = value.toString().split(Config.SepIndexes);
			String[] leftIndex = indices[0].split(Config.SepFilePosition);
			String[] rightindex = indices[1].split(Config.SepFilePosition);
			
			Path leftPath = new Path(leftIndex[0]);
			Path rightPath = new Path(rightindex[0]);
			long leftOffset = Long.parseLong(leftIndex[1]);
			long rightOffset = Long.parseLong(rightindex[1]);
			
			String delimiterRegex = delimiter + "+";
			Set<String> leftRecord = Helper.readRecordAsSet(fs, leftPath, leftOffset, delimiterRegex);
			Set<String> rightRecord = Helper.readRecordAsSet(fs, rightPath, rightOffset, delimiterRegex);
			
			Set<String> difference = Sets.difference(leftRecord, rightRecord);
			Set<String> intersection = Sets.intersection(leftRecord, rightRecord);
			
			Set<String> pattern = Sets.newTreeSet();

			if (leftRecord.size() <= minPatternLength)	// not possible!
				pattern.addAll(leftRecord);
			else
				do
				{
					pattern.clear();
					List<String> complement = Helper.sampleUniformly(difference, 1);
					List<String> intersect = Helper.sampleUniformly(intersection, 0);
					
					pattern.addAll(complement);
					pattern.addAll(intersect);
				} while (pattern.size() < minPatternLength);

			output.collect(key, new Text(StringUtils.join(delimiter, pattern)));
		}
	}

}
