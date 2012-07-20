package br.com.caelum.vraptor.mauth;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.ioc.ComponentFactory;

public interface Access<T> extends ComponentFactory<T>, PossibleUser {

	void login(Long id);

	Option<T> currentUser();

	void logout();
}
