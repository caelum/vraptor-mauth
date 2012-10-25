package br.com.caelum.vraptor.mauth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class URIVerifierTest {

	private static final String MY_HOST = "caelum.com.br";
	private static final String FULL_HOST = "http://" + MY_HOST;

	private URIVerifier verifier;

	@Mock
	private HttpServletRequest request;

	@Before
	public void setUp() throws Exception {
		verifier = new URIVerifier(request);
		when(request.getServerName()).thenReturn(MY_HOST);
	}

	@Test
	public void isInTheSameDomainIfRelative() {
		assertTrue(verifier.sameDomainAsMe("/someRelativeUri"));
		assertTrue(verifier.sameDomainAsMe("anotherRelativeUri"));
	}

	@Test
	public void isInTheSameDomainIfTheHostIsTheSameAsTheServerHostName() throws Exception {
		assertTrue(verifier.sameDomainAsMe(FULL_HOST));
		assertTrue(verifier.sameDomainAsMe(FULL_HOST + "/absoluteUri"));
	}

	@Test
	public void isNotInTheSameDomainIfTheHostIsDifferentFromMyHostName() throws Exception {
		assertFalse(verifier.sameDomainAsMe("http://google.com"));
	}

	@Test
	public void isNotInTheSameDomainIfTheGivenUriIsInvalid() throws Exception {
		assertFalse(verifier.sameDomainAsMe("as$saf-12424r12sax]]"));
	}
}
