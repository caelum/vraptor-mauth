package br.com.caelum.vraptor.mauth;

import org.hibernate.Session;

import br.com.caelum.vraptor.Option;

public class UserDAO {

	private final Session session;

	public UserDAO(Session session) {
		this.session = session;
	}

	public void update(User aUser) {
		session.update(aUser);

	}

	public void save(User aUser) {
		session.save(aUser);
	}

	public Option<User> findForEncryptedURL(String firstGenerated) {

		User user = (User) session
				.createQuery("select u from User as u where u.password.lastEncryptedRecoveryURL = :lastEncryptedRecoveryURL")
				.setParameter("lastEncryptedRecoveryURL", firstGenerated)
				.uniqueResult();

		return Option.valueOf(user);
	}

}
