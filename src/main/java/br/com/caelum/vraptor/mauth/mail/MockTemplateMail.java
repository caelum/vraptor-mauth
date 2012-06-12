package br.com.caelum.vraptor.mauth.mail;

import br.com.caelum.vraptor.mauth.SystemUser;

public class MockTemplateMail implements TemplateMail {

	private final MockTemplateMailer mailer;

	public MockTemplateMail(MockTemplateMailer mailer) {
		this.mailer = mailer;
	}

	@Override
	public TemplateMail with(String key, Object value) {
		return this;
	}

	@Override
	public void dispatchTo(SystemUser u) {
		mailer.notifySent(this, u);
	}

}
