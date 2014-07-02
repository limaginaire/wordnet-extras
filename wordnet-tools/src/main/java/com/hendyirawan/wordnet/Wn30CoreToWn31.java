package com.hendyirawan.wordnet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Convert WordNet 3.0 Core synsets to WordNet 3.1 Core synsets.
 *
 */
public class Wn30CoreToWn31 
{
	private static final Logger log = LoggerFactory
			.getLogger(Wn30CoreToWn31.class);

	/**
	 * Words are identified by means of the following URI scheme:
	 * 
	 * http://wordnet-rdf.princeton.edu/wn31/word-[nvarsp]
	 * 
	 * Where [nvarsp] is the part-of-speech. Similarly synsets are identified as follows
	 * 
	 * http://wordnet-rdf.princeton.edu/wn31/[9 digit code]-[nvarsp]
	 * 
	 * Where the 9-digit code is the identifier of the synset. The first digit of the synset identifies the part-of-speech according to the following table
	 * Part-of-speech	Letter code	Numeric code
	 * Noun	n	1
	 * Verb	v	2
	 * Adjective	a	3
	 * Adverb	r	4
	 * Adjective Satellite	s	3
	 * Phrase	p	4
	 */
	public static final ImmutableMap<Character, Integer> POS_NUMERIC = ImmutableMap.<Character, Integer>builder()
			.put('n', 1)
			.put('v', 2)
			.put('a', 3)
			.put('r', 4)
			.put('s', 3)
			.put('p', 4)
			.build();
	
    public static void main( String[] args ) throws IOException
    {
    	Preconditions.checkArgument(args.length == 2, "Usage: wn30core-to-wn31 path/to/wn30-core-synsets.tab dest/dir");
    	
    	ArrayList<String> wn31senses = new ArrayList<>();
    	
    	Model model = ModelFactory.createDefaultModel();
    	model.setNsPrefix("wn31", "http://wordnet-rdf.princeton.edu/wn31/");
    	model.setNsPrefix("wordnet-ontology", "http://wordnet-rdf.princeton.edu/ontology#");
    	model.setNsPrefix("olo", "http://purl.org/ontology/olo/core#");
    	model.setNsPrefix("xsd", XSD.getURI());
    	Resource synsetRes = model.createResource(model.getNsPrefixURI("wordnet-ontology") + "Synset");
    	Property indexProp = model.createProperty(model.getNsPrefixURI("olo"), "index");
    	
    	File wn30coreFile = new File(args[0]);
    	log.info("Loading WordNet 3.0 core file '{}'...", wn30coreFile);
    	try (CSVReader reader = new CSVReader(new FileReader(wn30coreFile), '\t')) {
    		int index = 1;
    		while (true) {
    			String[] row = reader.readNext();
    			if (row == null) {
    				break;
    			}
    			String wn30sense = row[0];
    			char posLetter = wn30sense.charAt(9);
    			final Integer posNumeric = Preconditions.checkNotNull(POS_NUMERIC.get(posLetter),
    					"Invalid part-of-speech letter code '%s' for sense '%s'", posLetter, wn30sense);
				String wn31sense = posNumeric + wn30sense;
				wn31senses.add(wn31sense);
    			final Resource wn31res = model.createResource(model.getNsPrefixURI("wn31") + wn31sense);
				model.add(wn31res, RDF.type, synsetRes);
				model.addLiteral(wn31res, indexProp, index); // hacky usage, but pragmatic :P
				index++;
    		}
    	}
    	
    	log.info("Converted to {} RDF statements", Iterators.size(model.listStatements()));
    	File wn31TurtleFile = new File(args[1], "wn31-core-synsets.ttl");
    	log.info("Saving TURTLE to '{}'...", wn31TurtleFile);
    	RDFDataMgr.write(new FileOutputStream(wn31TurtleFile), model, RDFFormat.TURTLE);
    	File wn31TsvFile = new File(args[1], "wn31-core-synsets.tab");
    	log.info("Saving TSV to '{}'...", wn31TsvFile);
    	try (CSVWriter writer = new CSVWriter(new FileWriter(wn31TsvFile), '\t', CSVWriter.NO_QUOTE_CHARACTER)) {
    		for (String wn31sense : wn31senses) {
    			writer.writeNext(new String[] { wn31sense });
    		}
    	}
    }
    
}
