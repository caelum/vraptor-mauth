package br.com.caelum.vraptor.mauth;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class URIVerifier {

	private final HttpServletRequest request;

	public URIVerifier(HttpServletRequest request) {
		this.request = request;
	}

	public boolean sameDomainAsMe(String uri) {
		try {
			String uriHost = new URI(uri).getHost();
			return uriHost == null || request.getServerName().equals(uriHost);
		} catch (URISyntaxException e) {
			return false;
		}
	}

}
