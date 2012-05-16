package br.com.caelum.vraptor.mauth;

public interface AuthConfiguration {

	<T extends SystemUser> Class<T> getUserType();

	String getTokenQuery();

}
