package br.com.caelum.vraptor.mauth.mail;

public interface TemplateMailer {

	TemplateMail template(String name, Object... nameParameters);

}