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
	private NavigationInfo navigationInfo = new NavigationInfo();
	
	public User() {
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email
				+ ", token=" + token + ", password=" + password
				+ ", navigationInfo=" + navigationInfo + "]";
	}

	public User(String name, String email, Password password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

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