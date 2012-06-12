package br.com.caelum.vraptor.mauth.mail;

import br.com.caelum.vraptor.mauth.SystemUser;

public interface TemplateMail {

	TemplateMail with(String key, Object value);

	void dispatchTo(SystemUser u);

}