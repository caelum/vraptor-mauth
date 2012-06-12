package br.com.caelum.vraptor.mauth;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.simplemail.template.TemplateMailer;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.PageResult;

@Open
@Resource
public class PasswordForgotController {
	private final AuthUserRepository users;
	private final Result result;
	private final Validator validator;
	private final TemplateMailer mailer;
	private final Authenticator auth;

	PasswordForgotController(Result result, AuthUserRepository users,
			Validator validator, TemplateMailer mailer, Authenticator auth) {
		this.result = result;
		this.users = users;
		this.validator = validator;
		this.mailer = mailer;
		this.auth = auth;
	}

	@Post("/auth/accountRecovery")
	public void forgotPassword(String email) {
		Option<SystemUser> userLoaded = users.findByEmail(email);
		if (userLoaded.isEmpty()) {
			result.include("error", "vraptor.email_not_found");
			result.redirectTo("/");
			return;
		}
		SystemUser u = userLoaded.get();
		String token = u.getPassword().generateEncryptedRecoveryText(
				u.getEmail());
		mailer.template("mail.passwordForgot").with("token", token)
				.dispatchTo(u.getName(), u.getEmail());

		result.include("error", "vraptor.forgot.password.email.sent");
		result.redirectTo("/");
	}

	@Get("/auth/resetPassword/{token}")
	public void resetPassword(String token) {
		Option<SystemUser> user = findUserByToken(token);

		String recoveryToken = null;
		if (user.isDefined()) {
			recoveryToken = user.get().getPassword()
					.getLastEncryptedRecoveryURL();
		}

		result.include("token", recoveryToken);
	}

	private Option<SystemUser> findUserByToken(String token) {
		Option<SystemUser> user = users.findForEncryptedURL(token);

		if (!user.isDefined()) {
			validator.add(new I18nMessage(
					"vraptor.mauth.forgotrequest.notfound",
					"vraptor.mauth.forgotrequest.notfound"));
			validator.onErrorUse(PageResult.class).forwardTo("/");
		}
		return user;
	}

	@Post("/auth/reassignPassword")
	public void reassignPassword(String newPassword, String token) {
		Option<SystemUser> search = findUserByToken(token);
		SystemUser user = search.get();
		user.getPassword().changeTo(newPassword);
		user.getPassword().generateEncryptedRecoveryText(user.getEmail());
		users.update(user);
		
		result.include("error", "vraptor.password_changed");

		result.redirectTo("/");
	}

}