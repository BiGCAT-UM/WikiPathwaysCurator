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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.unimaas.bigcat.wikipathways.curator.ResourceHelper;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;
import nl.unimaas.bigcat.wikipathways.curator.StringMatrix;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertEquals;
import nl.unimaas.bigcat.wikipathways.curator.assertions.AssertNotNull;
import nl.unimaas.bigcat.wikipathways.curator.assertions.IAssertion;

public class InteractionTests {

	public static List<IAssertion> all(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		assertions.addAll(noMetaboliteToNonMetaboliteConversions(helper));
		assertions.addAll(noNonMetaboliteToMetaboliteConversions(helper));
		assertions.addAll(noGeneProteinConversions(helper));
		return assertions;
	}

	public static List<IAssertion> noMetaboliteToNonMetaboliteConversions(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noMetaboliteNonMetaboliteConversions.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("InteractionTests", "noMetaboliteToNonMetaboliteConversions", table));
		Set<String> allowedProteinProducts = new HashSet<String>();
		allowedProteinProducts.add("http://identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
		allowedProteinProducts.add("http://identifiers.org/chebi/CHEBI:39026"); // LPL
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String targetID = table.get(i, "target");
				if (!allowedProteinProducts.contains(targetID)) {
					errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
							table.get(i, "metabolite") + " " + table.get(i, "target") + " " +
							table.get(i, "interaction") + "\n";
					errorCount++;
				} // else, OK, this is allows as conversion target
			}
		}
		assertions.add(new AssertEquals(
			"InteractionTests", "noMetaboliteToNonMetaboliteConversions", 
			0, errorCount, "Unexpected metabolite to non-metabolite conversions:\n" + errors
		));
		return assertions;
	}

	public static List<IAssertion> noNonMetaboliteToMetaboliteConversions(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noNonMetaboliteMetaboliteConversions.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("InteractionTests", "noNonMetaboliteToMetaboliteConversions", table));
		Set<String> allowedProducts = new HashSet<String>();
		    allowedProducts.add("http://identifiers.org/hmdb/HMDB04246"); // from KNG1, e.g. in WP
		    allowedProducts.add("http://identifiers.org/hmdb/HMDB0004246"); // from KNG1, e.g. in WP
		    allowedProducts.add("http://identifiers.org/hmdb/HMDB0061196"); // angiotensin, a peptide hormone
		    allowedProducts.add("http://identifiers.org/chebi/CHEBI:2718"); // angiotensin, a peptide hormone
		Set<String> allowedProteinSubstrates = new HashSet<String>();
            allowedProteinSubstrates.add("http://identifiers.org/uniprot/H9ZYJ2"); // theoredoxin, e.g. WP3580
            allowedProteinSubstrates.add("http://identifiers.org/chebi/CHEBI:39026"); // LDL
            allowedProteinSubstrates.add("http://identifiers.org/wikidata/Q381899"); // fibrinogen
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String metabolite = table.get(i, "metabolite");
				String nonmetabolite = table.get(i, "target");
				if (!allowedProducts.contains(metabolite) &&
						!allowedProteinSubstrates.contains(nonmetabolite)) {
					errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
							nonmetabolite + " " + metabolite + " " +
							table.get(i, "interaction") + "\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(
			"InteractionTests", "noNonMetaboliteToMetaboliteConversions",
			0, errorCount, "Unexpected non-metabolite to metabolite conversions:\n" + errors
		));
		return assertions;
	}

	public static List<IAssertion> noGeneProteinConversions(SPARQLHelper helper) throws Exception {
		List<IAssertion> assertions = new ArrayList<>();
		String sparql = ResourceHelper.resourceAsString("interactions/noGeneProteinConversions.rq");
		StringMatrix table = helper.sparql(sparql);
		assertions.add(new AssertNotNull("InteractionTests", "noGeneProteinConversions", table));
		Set<String> allowedProteinSubstrates = new HashSet<String>();
		allowedProteinSubstrates.add("http://identifiers.org/uniprot/P0DTD1"); // SARS-CoV-2 main protease
		String errors = "";
		int errorCount = 0;
		if (table.getRowCount() > 0) {
			// OK, but then it must be proteins, e.g. IFN-b
			for (int i=1; i<=table.getRowCount(); i++) {
				String protein = table.get(i, "protein");
				if (!allowedProteinSubstrates.contains(protein)) {
					errors += table.get(i, "organism") + " " + table.get(i, "pathway") + " -> " +
							protein + " " + table.get(i, "gene") + " " +
							table.get(i, "interaction") + " Did you mean wp:TranscriptionTranslation?\n";
					errorCount++;
				}
			}
		}
		assertions.add(new AssertEquals(
			"InteractionTests", "noGeneProteinConversions",
			0, errorCount, "Unexpected gene-protein conversions:\n" + errors
		));
		return assertions;
	}

}