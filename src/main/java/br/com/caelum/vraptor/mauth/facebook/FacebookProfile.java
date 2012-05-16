package br.com.caelum.vraptor.mauth.facebook;

public class FacebookProfile {

	private final String email;
	private final String name;
	private final String id;
	
	public FacebookProfile(String email, String name, String id) {
		super();
		this.email = email;
		this.name = name;
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getState() {
		return null;
	}

	public String getCity() {
		return null;
	}

	public String getId() {
		return this.id;
	}
	
}
