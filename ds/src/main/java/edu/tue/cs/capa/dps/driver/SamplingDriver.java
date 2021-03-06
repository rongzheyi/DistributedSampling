package edu.tue.cs.capa.dps.driver;

import org.apache.hadoop.util.ProgramDriver;

import edu.tue.cs.capa.dps.disc.CombinedDiscDriver;
import edu.tue.cs.capa.dps.disc.DiscDriver;
import edu.tue.cs.capa.dps.disc.expand.ExpanderDriver;
import edu.tue.cs.capa.dps.freq.FreqDriver;


public class SamplingDriver
{
	public static void main(String[] args)
	{
		int exitCode = -1;
		ProgramDriver pgd = new ProgramDriver();
		try
		{
			pgd.addClass("expand", ExpanderDriver.class, "expand lines to a fixed length");
			pgd.addClass("disc2", DiscDriver.class, "sample according to discriminativity");
			pgd.addClass("freq", FreqDriver.class, "sample according to frequency");
			pgd.addClass("disc", CombinedDiscDriver.class, "combine expander and disc sampler");
			pgd.driver(args);
			exitCode = 0;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		System.exit(exitCode);
	}

}
