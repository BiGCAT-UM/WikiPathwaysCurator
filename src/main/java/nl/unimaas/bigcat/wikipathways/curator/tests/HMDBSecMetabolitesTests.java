/* Copyright (C) 2013,2018-2020  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.unimaas.bigcat.wikipathways.curator.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.BridgeDbTiwidReader;
import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.assertions.Test;

public class HMDBSecMetabolitesTests {
	
	private static Map<String,String> oldToNew = new HashMap<String, String>();
	private static Set<String> nonExisting = new HashSet<String>();
	
	static {
		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/hmdb/HMDBDataSecondaryAll_Final.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}

		nonExisting.add("HMDB0002708"); // "How did you get here? That page doesn't exist. Oh well, it happens."

		// data from Tiwid
		Map<String,String> tiwidData = BridgeDbTiwidReader.parseCSV("tiwid/hmdb.csv");
		for (String identifier : tiwidData.keySet()) {
			if (tiwidData.get(identifier) != null) {
				oldToNew.put(identifier, tiwidData.get(identifier));
			} else {
				nonExisting.add(identifier);
			}
		}
	}

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(outdatedIdentifiers(helper));
		assertions.addAll(nonExisting(helper));
		assertions.addAll(oldFormat(helper));
		return assertions;
	}

	public static List<IAssertion> outdatedIdentifiers(SPARQLHelper helper) throws Exception {
		Test test = new Test("HMDBSecMetabolitesTests", "outdatedIdentifiers");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;

		// Testing
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.length() == 11) identifier.replace("HMDB00", "HMDB");
				if (oldToNew.containsKey(identifier)) {
					String primID = oldToNew.get(identifier);
					if (!primID.equals(identifier.replace("HMDB", "HMDB00"))) { // ignore longer format
						errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
							" has " + identifier + " but has primary identifier " + primID + "\n";
						errorCount++;
					}
				}
			}
		}		

		// Reporting
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Secondary HMDB identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> nonExisting(SPARQLHelper helper) throws Exception {
		Test test = new Test("HMDBSecMetabolitesTests", "nonExisting");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;

		// Testing
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (nonExisting.contains(identifier)) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but it does not exist\n";
					errorCount++;
				}
			}
		}

		// Reporting
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Non-existing HMDB identifiers detected: " + errorCount, errors
		));
		return assertions;
	}

	public static List<IAssertion> oldFormat(SPARQLHelper helper) throws Exception {
		Test test = new Test("HMDBSecMetabolitesTests", "oldFormat");
		// Getting the data
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("metabolite/allHMDBIdentifiers.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull(test, table));
		String errors = "";
		int errorCount = 0;

		// Testing
		if (table.getRowCount() > 0) {
			for (int i=1; i<=table.getRowCount(); i++) {
				String identifier = table.get(i, "identifier");
				if (identifier.length() < 11) {
					errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						" has " + identifier + " but the new format is " + identifier.replace("HMDB", "HMDB00") + "\n";
					errorCount++;
				}
			}
		}

		// Reporting
		assertions.add(new AssertEquals(test, 
			0, errorCount, "Old HMDB identifier format detected: " + errorCount, errors
		));
		return assertions;
	}

}
