package br.com.caelum.gnarus.auth;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.mauth.PossibleUser;

public interface Access<T> extends ComponentFactory<T>, PossibleUser {

	public void login(Long id);

	public Option<T> currentUser();

	public void logout();
}
