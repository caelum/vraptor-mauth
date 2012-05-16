package br.com.caelum.vraptor.mauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DigesterTest {

	@Test
	public void should_recover_digested_id() {
		for(long x = 1; x < 10000; x++) {
			String hash = Digester.hashFor(x);
			assertEquals(x, Digester.idFor(hash).longValue());
		}
	}

}
