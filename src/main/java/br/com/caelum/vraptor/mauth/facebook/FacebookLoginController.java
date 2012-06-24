package br.com.caelum.vraptor.mauth.facebook;

import static br.com.caelum.vraptor.view.Results.page;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mauth.Authenticator;
import br.com.caelum.vraptor.mauth.Open;
import br.com.caelum.vraptor.mauth.SystemUser;
import br.com.caelum.vraptor.validator.I18nMessage;

@Resource
@Open
@SuppressWarnings({"unchecked","rawtypes"})
public class FacebookLoginController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory
			.getLogger(FacebookLoginController.class);

	private final Facebook facebook;

	private final Authenticator auth;

	private final Result result;

	private final Validator validator;

	public FacebookLoginController(Validator validator, Facebook facebook, Authenticator auth,
			Result result) {
		this.validator = validator;
		this.result = result;
		this.facebook = facebook;
		this.auth = auth;
	}

	@Post("/auth/facebook")
	public void login(final String profile) {
		LOGGER.debug("logging in user " + profile);
		final SystemUser user = facebook.connectedOrFindUserFor(profile);
		SystemUser toLogin = facebook.createOrConnectUser(profile, user);
		if (toLogin == null) {
			validator.add(new I18nMessage("vraptor.inexisting_facebook_user",
					"vraptor.inexisting_facebook_user"));
			validator.onErrorUse(page()).redirectTo("/");
		} else {
			auth.authenticate(toLogin);
		}
		result.redirectTo("/");
	}

}
