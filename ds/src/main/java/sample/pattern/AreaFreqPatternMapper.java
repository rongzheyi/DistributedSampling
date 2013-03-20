package sample.pattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import util.Parameters;


public class AreaFreqPatternMapper extends AbstractPatternMapper
{
	// FIXME: wrong!
	private <T> List<T> sampleWeighted(List<T> items)
	{
		int k = 0;
		double key = 0.0;

		// sample k~id(1..n), reservoir size=1
		for (int i = 1; i <= items.size(); i++)
		{
			double currKey = Math.pow(rng.nextDouble(), 1.0 / i);
			if (currKey > key)
			{
				key = currKey;
				k = i;
			}
		}

		// sample F~(|F|=k), reservoir size = k
		ArrayList<T> res = new ArrayList<T>(k);
		int count = 0;
		for (T item : items)
		{
			count++;
			if (count <= k)
				res.add(item);
			else
			{
				int r = rng.nextInt(count);
				if (r < k) res.set(r, item);
			}
		}

		return res;
	}


	@Override
	public void map(NullWritable key, Text value, OutputCollector<NullWritable, Text> output, Reporter reporter)
					throws IOException
	{
		Path inputfilepath = new Path(leftPath);
		long offset = Long.parseLong(value.toString());

		String[] record = readRecord(fs, inputfilepath, offset).split(Parameters.SepItems);

		List<String> pattern = sampleWeighted(Arrays.asList(record));

		if (pattern.size() == 0) return;

		output.collect(NullWritable.get(), new Text(composePattern(pattern)));
	}

}