package br.com.caelum.vraptor.mauth;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

	private static final String DASHBOARD = "/dashboard";
	private @Mock
	Authenticator<User> authenticator;
	private @Mock
	Validator validator;
	private @Mock
	URIVerifier uriVerifier;
	private MockResult result;
	private LoginController controller;
	private MockUser userToSignIn;
	private String userPassword;

	@Before
	public void before() {
		this.result = spy(new MockResult());
		this.validator = spy(new MockValidator());
		this.controller = new LoginController(authenticator, result,
				validator, uriVerifier);
		this.userToSignIn = new MockUser();
		this.userPassword = "john";
	}

	@Test
	public void call_the_authenticator_with_the_user() {
		when(authenticator.authenticate(userToSignIn.getEmail(),userPassword)).thenReturn(true);

		controller.signin(userToSignIn.getEmail(), userPassword, null);

		verify(authenticator).authenticate(userToSignIn.getEmail(), userPassword);
		verify(result).redirectTo(DASHBOARD);
	}

	@Test
	public void call_the_authenticator_with_the_user_and_redirect_to_the_last_url_requested() {
		MockUser userToSignIn = new MockUser();
		String password = "john";
		String url = "/blabla";

		when(authenticator.authenticate(userToSignIn.getEmail(),password)).thenReturn(true);
		when(uriVerifier.sameDomainAsMe(url)).thenReturn(true);
		controller.signin(userToSignIn.getEmail(), password, url);

		verify(authenticator).authenticate(userToSignIn.getEmail(), password);
		verify(result).redirectTo(url);
	}

	@Test
	public void call_the_authenticator_with_the_user_and_redirect_to_the_dashboard_if_requested_url_is_not_mine() {
		MockUser userToSignIn = new MockUser();
		String password = "john";
		String url = "http://google.com";

		when(authenticator.authenticate(userToSignIn.getEmail(),password)).thenReturn(true);
		when(uriVerifier.sameDomainAsMe(url)).thenReturn(false);
		controller.signin(userToSignIn.getEmail(), password, url);

		verify(authenticator).authenticate(userToSignIn.getEmail(), password);
		verify(result).redirectTo(DASHBOARD);
	}

	@Test
	public void call_the_authenticator_to_sign_out() {
		controller.signout();
		verify(authenticator).signout();
	}

}
