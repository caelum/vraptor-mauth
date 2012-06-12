package br.com.caelum.vraptor.mauth.mail;

import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.mauth.SystemUser;


public class MockTemplateMailer implements TemplateMailer {

	private final Map<SystemUser, MockTemplateMail> sentMails = new HashMap<SystemUser, MockTemplateMail>();

	@Override
	public TemplateMail template(String name, Object... nameArgs) {
		return new MockTemplateMail(this);
	}

	public boolean sentEmailTo(SystemUser user) {
		return sentMails.get(user) != null;
	}

	public void notifySent(MockTemplateMail mail, SystemUser u) {
		sentMails.put(u, mail);
	}

}
