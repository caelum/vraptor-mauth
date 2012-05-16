package br.com.caelum.vraptor.mauth.facebook;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.mauth.AuthUserRepository;
import br.com.caelum.vraptor.mauth.Open;
import br.com.caelum.vraptor.mauth.PossibleUser;
import br.com.caelum.vraptor.mauth.SystemUser;
import br.com.caelum.vraptor.mauth.SystemUserCreator;
import br.com.caelum.vraptor.mauth.Transaction;

@Resource
@Open
@SuppressWarnings({"rawtypes","unchecked"})
public class FacebookLoginController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory
			.getLogger(FacebookLoginController.class);

	private final PossibleUser possibleUser;
	private final AuthUserRepository users;
	private final Transaction transaction;
	private final SystemUserCreator creator;

	public FacebookLoginController(PossibleUser possibleUser,
			AuthUserRepository users, Transaction transaction,
			SystemUserCreator creator) {
		this.possibleUser = possibleUser;
		this.users = users;
		this.transaction = transaction;
		this.creator = creator;
	}

	@Get("/auth/facebook")
	public void login(final FacebookProfile profile) {
		LOGGER.debug("logging in user " + profile.getId());
		final SystemUser user = connectedOrFindUserFor(profile);
		transaction.execute(new Runnable() {
			public void run() {
				createOrConnectUser(profile, user);
			}
		});

	}

	private SystemUser connectedOrFindUserFor(FacebookProfile profile) {
		if (possibleUser.isSignedIn()) {
			return possibleUser.get();
		}
		Option<SystemUser> possible = users.findByEmail(profile.getEmail());
		return possible.getOrNull();
	}

	private void createOrConnectUser(FacebookProfile profile, SystemUser user) {
		if (user == null) {
			creator.create(profile);
		} else {
			creator.connect(user, profile);
		}
	}

}
