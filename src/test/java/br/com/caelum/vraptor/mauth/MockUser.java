package br.com.caelum.vraptor.mauth;

import java.io.Serializable;

import br.com.caelum.vraptor.mauth.user.NavigationInfo;

public class MockUser implements SystemUser {

	private Password password = new Password();

	@Override
	public String getEmail() {
		return "john@malcovich.com";
	}

	public String getName() {
		return "john malcovich";
	}

	@Override
	public void setToken(String token) {
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public Serializable getId() {
		return null;
	}

	@Override
	public Password getPassword() {
		return password;
	}

	@Override
	public NavigationInfo getNavigationInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
