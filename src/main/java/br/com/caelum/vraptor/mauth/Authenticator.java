package br.com.caelum.vraptor.mauth;

public interface Authenticator<T extends SystemUser> {

	public abstract boolean authenticate(String email, String password);

	public abstract void loginWithoutPasswordCheck(Long id);

	public abstract void signout();

	/**
	 * Force down a authentication with this user.
	 */
	void authenticate(T toLogin);

}