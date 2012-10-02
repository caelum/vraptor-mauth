package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.view.Results.page;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;

@Resource
@Open
@SuppressWarnings("rawtypes")
public class LoginController {
	private final Authenticator authenticator;
	private final Result result;
	private final Validator validator;

	public LoginController(Authenticator authenticator, Result result,
			Validator validator) {
		this.authenticator = authenticator;
		this.result = result;
		this.validator = validator;
	}

	@Post("/signin")
	public void signin(String email, String password, String urlAfterLogin) {
		boolean isAuthenticated = authenticator.authenticate(email, password);
		if (!isAuthenticated) {
			validator.add(new I18nMessage("vraptor.mauth.signin.fail",
					"vraptor.mauth.signin.fail"));
			result.include("email", email);
			validator.onErrorUse(page()).redirectTo("/");
		}

		if (urlAfterLogin == null) {
			result.redirectTo("/dashboard");
		} else {
			result.redirectTo(urlAfterLogin);
		}
	}
	
	@Post("/easySigin")
	public void signin(String email, String password) {
		boolean isAuthenticated = authenticator.authenticate(email, password);
		if (!isAuthenticated) {
			result.use(Results.status()).notAcceptable();
			return;
		}
		result.use(Results.status()).accepted()
	}

	@Get
	public void signinHack(Long id) {
		authenticator.loginWithoutPasswordCheck(id);
		result.redirectTo("/dashboard");
	}

	@Path("/signout")
	public void signout() {
		authenticator.signout();
		result.redirectTo("/");
	}

}
