package br.com.caelum.vraptor.mauth;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import br.com.caelum.vraptor.mauth.user.NavigationInfo;

@Entity
class User implements SystemUser {
	
	@GeneratedValue
	@Id
	private Long id;
	private String name,email,token;
	private Password password;
	private NavigationInfo navigationInfo;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public Password getPassword() {
		return password;
	}
	public NavigationInfo getNavigationInfo() {
		return navigationInfo;
	}
	
}