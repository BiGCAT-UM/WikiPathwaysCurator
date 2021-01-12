/* Copyright (C) 2018-2021  Egon Willighagen <egon.willighagen@gmail.com>
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
package nl.unimaas.bigcat.wikipathways.curator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;
import nl.unimaas.bigcat.wikipathways.curator.tests.CASMetabolitesTests;
import nl.unimaas.bigcat.wikipathways.curator.tests.ChEBIMetabolitesTests;

public class ChEBIMetabolites extends JUnitTests {

	private static Map<String,String> oldToNew = new HashMap<String, String>();

	private static List<String> nonexisting = new ArrayList<String>();
	static {{
		  nonexisting.add("443041");
		  nonexisting.add("594834");
	}}

	@BeforeAll
	public static void loadData() throws InterruptedException {
		if (System.getProperty("SPARQLEP").startsWith("http")) {
			// ok, assume the SPARQL end point is online
			System.err.println("SPARQL EP: " + System.getProperty("SPARQLEP"));
		} else {
			Model data = OPSWPRDFFiles.loadData();
			Assertions.assertTrue(data.size() > 5000);
		}

		// now load the deprecation data
		String deprecatedData = ResourceHelper.resourceAsString("metabolite/chebi/deprecated.csv");
		String lines[] = deprecatedData.split("\\r?\\n");
		for (int i=0; i<lines.length; i++) {
			String[] ids = lines[i].split(",");
			oldToNew.put(ids[0], ids[1]);
		}
		System.out.println("size: " + oldToNew.size());
	}

	@BeforeEach
	public void waitForIt() throws InterruptedException { Thread.sleep(OPSWPRDFFiles.SLEEP_TIME); }

	@Test
	public void secondaryChEBIIdentifiers() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
				List<IAssertion> assertions = ChEBIMetabolitesTests.secondaryChEBIIdentifiers(helper);
				performAssertions(assertions);
			});
		});
	}

	@Test
	public void faultyChEBIIdentifiers() throws Exception {
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			SPARQLHelper helper = (System.getProperty("SPARQLEP").contains("http:"))
				? new SPARQLHelper(System.getProperty("SPARQLEP"))
			    : new SPARQLHelper(OPSWPRDFFiles.loadData());
			Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
				List<IAssertion> assertions = ChEBIMetabolitesTests.faultyChEBIIdentifiers(helper);
				performAssertions(assertions);
			});
		});
	}

	@Test
	public void faultyChEBIChEBIIdentifiers() throws Exception {
		Assertions.assertNotSame(0, nonexisting.size(), "Unepxected empty list of unexpected identifiers");
		String sparql = ResourceHelper.resourceAsString("metabolite/allChEBIIdentifiers.rq");
		Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
			    : SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
		    String errors = "";
		    int errorCount = 0;
		    if (table.getRowCount() > 0) {
		    	for (int i=1; i<=table.getRowCount(); i++) {
		    		String identifier = table.get(i, "identifier");
		    		if (identifier.startsWith("ChEBI:")) {
		    			errors += table.get(i, "homepage") + " " + table.get(i, "label").replace('\n', ' ') +
						    " has a faulty identifier " +
						    identifier + " (it should be a number or start with \"CHEBI:\")\n";
		    			errorCount++;
		    		}
		    	}
		    }
		    Assertions.assertEquals(
		    	0, errorCount, "Faulty ChEBI identifiers detected:\n" + errors
		    );
		});
	}

	@Test
	public void chebiDataTypo() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/chebi.rq");
		Assertions.assertNotNull(sparql);
		Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
			StringMatrix table = (System.getProperty("SPARQLEP").contains("http:"))
				? SPARQLHelper.sparql(System.getProperty("SPARQLEP"), sparql)
				: SPARQLHelper.sparql(OPSWPRDFFiles.loadData(), sparql);
			Assertions.assertNotNull(table);
			Assertions.assertTrue(table.getRowCount() < 1, "Typo 'CHEBI' data sources (use 'ChEBI'):\n" + table);
		});
	}
}
