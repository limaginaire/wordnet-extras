package com.hendyirawan.wordnet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Preconditions;

/**
 * Convert WordNet 3.0 Core synsets to WordNet 3.1 Core synsets.
 *
 */
public class Wn30MsaToWn31 
{
	private static final Logger log = LoggerFactory
			.getLogger(Wn30MsaToWn31.class);

    public static void main( String[] args ) throws IOException
    {
    	Preconditions.checkArgument(args.length == 2, "Usage: wn30msa-to-wn31 path/to/wn-msa-all.tab dest/dir");
    	
    	File wn30coreFile = new File(args[0]);
    	File wn31TsvFile = new File(args[1], "wn31-msa-all.tab");
    	log.info("Loading wn30-msa '{}' (and converting to wn31-msa '{}')...", wn30coreFile, wn31TsvFile);
    	int index = 1;
    	try (CSVWriter writer = new CSVWriter(new FileWriter(wn31TsvFile), '\t', CSVWriter.NO_QUOTE_CHARACTER)) {
        	try (CSVReader reader = new CSVReader(new FileReader(wn30coreFile), '\t')) {
        		while (true) {
        			String[] row = reader.readNext();
        			if (row == null) {
        				break;
        			}
        			String wn30sense = row[0];
        			char posLetter = wn30sense.charAt(9);
        			final Integer posNumeric = Preconditions.checkNotNull(Wn30ToWn31.POS_NUMERIC.get(posLetter),
        					"Invalid part-of-speech letter code '%s' for sense '%s'", posLetter, wn30sense);
    				String wn31sense = posNumeric + wn30sense;
    				
    				writer.writeNext(new String[] { wn31sense, row[1], row[2], row[3] });
    				index++;
        		}
        	}
    	}
    	
    	log.info("Converted {} rows", index);
    }
    
}
