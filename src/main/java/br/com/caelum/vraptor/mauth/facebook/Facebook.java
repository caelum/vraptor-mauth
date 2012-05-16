package br.com.caelum.vraptor.mauth.facebook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.mauth.AuthUserRepository;
import br.com.caelum.vraptor.mauth.PossibleUser;
import br.com.caelum.vraptor.mauth.SystemUser;
import br.com.caelum.vraptor.mauth.SystemUserCreator;

@Component
@SuppressWarnings({"rawtypes","unchecked"})
class Facebook {

	private final PossibleUser possibleUser;
	private final AuthUserRepository users;
	private final SystemUserCreator creator;

	public Facebook(PossibleUser possibleUser, AuthUserRepository users,
			SystemUserCreator creator) {
		this.possibleUser = possibleUser;
		this.users = users;
		this.creator = creator;
	}

	SystemUser connectedOrFindUserFor(String profile) {
		if (possibleUser.isSignedIn()) {
			return possibleUser.get();
		}
		Option<SystemUser> possible = users.findByEmail(extractEmail(profile));
		return possible.getOrNull();
	}

	private static final Pattern EMAIL = Pattern.compile(".*email\\s*:\\s*\\\"([^\\\"]*)\\\".*");
	static String extractEmail(String profile) {
		Matcher matcher = EMAIL.matcher(profile);
		matcher.matches();
		return matcher.group(1);
	}

	public void createOrConnectUser(String profile, SystemUser user) {
		if (user == null) {
			creator.create(profile);
		} else {
			creator.connect(user, profile);
		}
	}

}
