package br.com.caelum.vraptor.mauth;

public interface Authenticator {

	public abstract boolean authenticate(String email, String password);

	public abstract void loginWithoutPasswordCheck(Long id);

	public abstract void signout();

}