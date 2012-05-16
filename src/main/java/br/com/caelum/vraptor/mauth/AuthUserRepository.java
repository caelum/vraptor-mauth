package br.com.caelum.vraptor.mauth;

import org.hibernate.Session;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class AuthUserRepository {

	private final Transaction tx;
	private final Session session;
	private final AuthConfiguration config;

	public AuthUserRepository(Session session, Transaction tx,
			AuthConfiguration config) {
		this.session = session;
		this.tx = tx;
		this.config = config;
	}

	public Option<SystemUser> accessed(final String token) {
		final SystemUser found = (SystemUser) session
				.createQuery(config.getTokenQuery())
				.setParameter("token", token).uniqueResult();
		if (found == null)
			return Option.none();
		tx.execute(new Runnable() {
			public void run() {
				found.getNavigationInfo().ping();
			}
		});
		return Option.some(found);
	}

	public SystemUser load(final SystemUser toLoad) {
		final SystemUser found = (SystemUser) session.get(config.getUserType(),
				toLoad.getId());
		return setupAfterLoad(found);
	}

	private SystemUser setupAfterLoad(final SystemUser found) {
		if (found != null) {
			tx.execute(new Runnable() {
				public void run() {
					String md5 = Digester.encrypt(found.getEmail()
							+ System.currentTimeMillis());
					found.setToken(md5);
				}
			});
		}
		return found;
	}

	public void logout(final SystemUser user) {
		if (user == null)
			return;
		tx.execute(new Runnable() {
			@Override
			public void run() {
				user.setToken(null);
			}
		});
	}

	public Option<SystemUser> findForEncryptedURL(String encryptedString) {
		SystemUser user = (SystemUser) session
				.createQuery(
						"from "
								+ getName()
								+ " where password.lastEncryptedRecoveryURL = :encryptedString")
				.setParameter("encryptedString", encryptedString)
				.uniqueResult();
		return Option.valueOf(user);
	}

	public void update(SystemUser user) {
		session.update(user);
	}

	public Option<SystemUser> findByEmail(String email) {
		SystemUser user = (SystemUser) session
				.createQuery("from " + getName() + " where email = :email")
				.setParameter("email", email).uniqueResult();
		return Option.valueOf(user);
	}

	private String getName() {
		return config.getUserType().getName();
	}

	public SystemUser load(Long id) {
		return setupAfterLoad((SystemUser) session.load(config.getUserType(), id));
	}

}
