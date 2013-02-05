package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.view.Results.page;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.Results;

@Resource
@Open
@SuppressWarnings("rawtypes")
public class LoginController {
	private final Authenticator authenticator;
	private final Result result;
	private final Validator validator;
	private final URIVerifier uriVerifier;

	public LoginController(Authenticator authenticator, Result result,
			Validator validator, URIVerifier uriVerifier) {
		this.authenticator = authenticator;
		this.result = result;
		this.validator = validator;
		this.uriVerifier = uriVerifier;
	}

	@Post("/signin")
	public void signin(String email, String password, String urlAfterLogin, String uriOnError) {
		boolean isAuthenticated = authenticator.authenticate(email, password);
		if (!isAuthenticated) {
			validator.add(new I18nMessage("vraptor.mauth.signin.fail",
					"vraptor.mauth.signin.fail"));
			result.include("email", email);
			result.include("lastUrl", urlAfterLogin);
			if(uriOnError == null || uriOnError.equals("") || !uriVerifier.sameDomainAsMe(uriOnError)) {
				validator.onErrorUse(page()).redirectTo("/");
			} else {
				validator.onErrorUse(page()).redirectTo(uriOnError);
			}
		}

		if (urlAfterLogin == null || !uriVerifier.sameDomainAsMe(urlAfterLogin)) {
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
		result.use(Results.status()).accepted();
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
