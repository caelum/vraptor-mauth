package br.com.caelum.vraptor.mauth;

import java.io.Serializable;

import br.com.caelum.vraptor.mauth.user.NavigationInfo;

public interface SystemUser {
	
	String getEmail();
	String getName();
	void setToken(String token);
	String getToken();
	Serializable getId();
	Password getPassword();
	NavigationInfo getNavigationInfo();

}
