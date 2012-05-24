package br.com.caelum.vraptor.mauth;


public class OurAuthConfiguration implements AuthConfiguration{

	@Override
	public Class<User> getUserType() {
		return User.class;
	}

	public String getTokenQuery() {
		return "from User as u where u.token = :token";
	}

}
