package br.com.caelum.vraptor.mauth;

import org.apache.commons.mail.EmailException;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.simplemail.Mailer;
import br.com.caelum.vraptor.simplemail.template.TemplateMailer;

@Open
@Resource
@SuppressWarnings("rawtypes")
public class PasswordForgotController {
	private final AuthUserRepository users;
	private final Result result;
	private final TemplateMailer mailer;
	private final Authenticator auth;
	private final Mailer realMailer;

	PasswordForgotController(Result result, AuthUserRepository users,
			TemplateMailer mailer, Authenticator auth, Mailer realMailer) {
		this.result = result;
		this.users = users;
		this.mailer = mailer;
		this.auth = auth;
		this.realMailer = realMailer;
	}

	@Post("/auth/accountRecovery")
	public void forgotPassword(String email) throws EmailException {
		Option<SystemUser> userLoaded = users.findByEmail(email);
		if (userLoaded.isEmpty()) {
			result.include("error", "vraptor.email_not_found");
			result.redirectTo("/");
			return;
		}
		SystemUser u = userLoaded.get();
		String token = u.getPassword().generateEncryptedRecoveryText(
				u.getEmail());
		realMailer.send(mailer.template("mail.passwordForgot")
				.with("token", token).to(u.getName(), u.getEmail()));

		result.include("error", "vraptor.forgot.password.email.sent");
		result.redirectTo("/");
	}

	@Get("/auth/resetPassword/{token}")
	public void resetPassword(String token) {
		Option<SystemUser> user = users.findForEncryptedURL(token);

		if (user.isEmpty()) {
			result.include("error", "vraptor.forgot.password.invalid_token");
			result.redirectTo("/");
			return;
		}

		String recoveryToken = user.get().getPassword()
				.getLastEncryptedRecoveryURL();
		result.include("token", recoveryToken);
	}

	@SuppressWarnings("unchecked")
	@Post("/auth/reassignPassword")
	public void reassignPassword(String newPassword, String token) {
		Option<SystemUser> search = users.findForEncryptedURL(token);
		SystemUser user = search.get();
		user.getPassword().changeTo(newPassword);
		user.getPassword().generateEncryptedRecoveryText(user.getEmail());
		users.update(user);
		auth.authenticate(user);

		result.include("error", "vraptor.password_changed");

		result.redirectTo("/");
	}

}