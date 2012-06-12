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
	private final Transaction tx;
	private final Validator validator;
	private final TemplateMailer mailer;

	PasswordForgotController(Result result, AuthUserRepository users,
			Transaction tx, Validator validator, TemplateMailer mailer) {
		this.result = result;
		this.users = users;
		this.tx = tx;
		this.validator = validator;
		this.mailer = mailer;
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
			.dispatchTo(u.getName(),u.getEmail());

		result.include("error", "vraptor.forgot.password.email.sent");
		result.redirectTo("/");
	}

	@Get("/resetPassword/{token}")
	public void resetPassword(String token) {
		final Option<SystemUser> user = findUserByToken(token);

		tx.execute(new Runnable() {
			public void run() {
				SystemUser found = user.get();
				found.getPassword().generateEncryptedRecoveryText(
						found.getEmail());
			}
		});

		String recoveryToken = null;
		if (user.isDefined()) {
			recoveryToken = user.get().getPassword()
					.getLastEncryptedRecoveryURL();
		}

		result.include("token", recoveryToken);
	}

	private Option<SystemUser> findUserByToken(String token) {
		final Option<SystemUser> user = users.findForEncryptedURL(token);

		if (!user.isDefined()) {
			validator.add(new I18nMessage(
					"vraptor.mauth.forgotrequest.notfound",
					"vraptor.mauth.forgotrequest.notfound"));
			validator.onErrorUse(PageResult.class).forwardTo("/");
		}
		return user;
	}

	@Post("/reassignPassword")
	public void reassignPassword(String newPassword, String token) {
		Option<SystemUser> search = findUserByToken(token);
		SystemUser user = search.get();
		user.getPassword().changeTo(newPassword);
		user.getPassword().generateEncryptedRecoveryText(user.getEmail());
		users.update(user);

		result.redirectTo("/");
	}

}