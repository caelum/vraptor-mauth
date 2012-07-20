package br.com.caelum.vraptor.mauth;

import br.com.caelum.gnarus.auth.Access;
import br.com.caelum.vraptor.environment.Environment;

public abstract class BasicAuthenticator<T extends SystemUser> implements Authenticator<T> {

	private final Environment env;
	private final Access<T> system;

	public BasicAuthenticator(Environment env, Access<T> system) {
		this.env = env;
		this.system = system;
	}
	@Override
	public void loginWithoutPasswordCheck(Long id) {
		if(env.getName().equals("development") || env.getName().equals("testing")) {
			system.login(id);
		}
	}

	@Override
	public void signout() {
		system.logout();
	}

	public void authenticate(User toLogin) {
		system.login(toLogin.getId());
	}

}
