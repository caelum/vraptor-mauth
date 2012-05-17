package br.com.caelum.vraptor.mauth.facebook;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FacebookTest {

	@Test
	public void shouldExtractHisEmail() {
		String email = Facebook
				.extractEmail("profile : { \"email\" : \"guilherme.silveira@caelum.com.br\" }");
		assertEquals("guilherme.silveira@caelum.com.br", email);
	}

}
