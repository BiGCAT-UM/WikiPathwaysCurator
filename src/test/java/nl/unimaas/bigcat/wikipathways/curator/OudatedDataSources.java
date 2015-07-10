/* Copyright (C) 2014  Egon Willighagen <egon.willighagen@gmail.com>
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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class OudatedDataSources {

	@Test(timeout=10000)
	public void outdatedUniprot() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Outdated 'Uniprot' data sources (use 'Uniprot-TrEMBL'):\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void outdatedUniprot2() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot2.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Outdated 'UniProt/TrEMBL' data sources (use 'Uniprot-TrEMBL'):\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void outdatedUniprot3() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot3.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Outdated 'Uniprot/TrEMBL' data sources (use 'Uniprot-TrEMBL'):\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void outdatedUniprot4() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/uniprot4.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Outdated 'UniProt' data sources (use 'Uniprot-TrEMBL'):\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void oldUniprotSwissProt() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/swissprot.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Outdated 'Uniprot-SwissProt' data sources (use 'Uniprot-TrEMBL'):\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void wrongPubChem() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/pubchem.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		// the metabolite test pathway has one outdated PubChem deliberately (WP2582)
		Assert.assertTrue("Outdated 'PubChem' data sources (use 'PubChem-compound' or 'PubChem-substance'):\n" + table, table.getRowCount() <= 1);
	}

	@Test(timeout=10000)
	public void noInChIDataSourceYet() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/inchi.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Don't use 'InChI' data sources yet, but found:\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void noInChIKeyDataSourceYet() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/inchikey.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertEquals("Don't use 'InChIKey' data sources yet, but found:\n" + table, 0, table.getRowCount());
	}

	@Test(timeout=10000)
	public void outdatedKeggCompoundDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggcompound.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
		Assert.assertTrue("Outdated 'Kegg Compound' data sources (use 'KEGG Compound'):\n" + table, table.getRowCount() <= 1);
	}

	@Ignore
	public void outdatedKeggOrthologDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggortholog.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		// the metabolite test pathway has one outdated Kegg Compound deliberately (WP2582)
		Assert.assertTrue("Outdated 'Kegg ortholog' data sources:\n" + table, table.getRowCount() <= 1);
	}

	@Test(timeout=10000)
	public void outdatedKeggEnzymeDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/keggenzyme.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		// the metabolite test pathway has one outdated Kegg enzyme deliberately (WP2582)
		Assert.assertTrue("Outdated 'Kegg enzyme' data sources:\n" + table, table.getRowCount() <= 1);
	}

	@Test(timeout=10000)
	public void outdatedEnsemblMouseDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertTrue("Outdated 'Ensembl Mouse' data sources (use 'Ensembl'):\n" + table, table.getRowCount() < 1);
	}

	@Test(timeout=10000)
	public void outdatedEnsemblHumanDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl2.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertTrue("Outdated 'Ensembl Human' data sources (use 'Ensembl'):\n" + table, table.getRowCount() < 1);
	}

	@Test(timeout=10000)
	public void outdatedEnsemblYeastDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl3.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertTrue("Outdated 'Ensembl Yeast' data sources (use 'Ensembl'):\n" + table, table.getRowCount() < 1);
	}

	@Test(timeout=10000)
	public void outdatedEnsemblCowDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ensembl4.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertTrue("Outdated 'Ensembl Cow' data sources (use 'Ensembl'):\n" + table, table.getRowCount() < 1);
	}

	@Test(timeout=10000)
	public void outdatedECNumberDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/ecNumber.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertTrue("Outdated 'EC Number' data sources (use 'Enzyme Nomenclature'):\n" + table, table.getRowCount() < 1);
	}

	@Test(timeout=10000)
	public void outdatedChemSpiderDataSource() throws Exception {
		String sparql = ResourceHelper.resourceAsString("outdated/chemspider.rq");
		Assert.assertNotNull(sparql);
		StringMatrix table = SPARQLHelper.sparql("http://sparql.wikipathways.org/", sparql);
		Assert.assertNotNull(table);
		Assert.assertTrue("Outdated 'ChemSpider' data sources (use 'Chemspider'):\n" + table, table.getRowCount() < 1);
	}
}
