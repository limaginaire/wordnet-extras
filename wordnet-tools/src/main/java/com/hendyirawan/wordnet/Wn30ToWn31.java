package com.hendyirawan.wordnet;

import com.google.common.collect.ImmutableMap;

public class Wn30ToWn31 {

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
	
}
