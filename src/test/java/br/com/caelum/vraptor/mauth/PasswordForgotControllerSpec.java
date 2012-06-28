package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.Option.none;
import static br.com.caelum.vraptor.Option.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.simplemail.template.MockTemplateMailer;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.view.HttpResult;


@RunWith(MockitoJUnitRunner.class)
public class PasswordForgotControllerSpec {

	private @Mock
	AuthUserRepository users;
	private MockResult result;
	private MockValidator validator;
	private PasswordForgotController controller;
	@Mock
	private HttpResult http;
	private MockTemplateMailer mailer;
	@Mock
	private Authenticator auth;

	@Before
	public void before() {
		this.result = spy(new MockResult());
		this.validator = spy(new MockValidator());
		this.mailer = new MockTemplateMailer();
		this.controller = new PasswordForgotController(result, users,
				validator, mailer, this.auth);
	}

	@Test
	public void complain_if_user_email_is_not_found() {
		when(users.findByEmail(null)).thenReturn(noUser());

		SystemUser guilherme = new MockUser();
		when(users.findByEmail(guilherme.getEmail())).thenReturn(noUser());

		when(result.use(HttpResult.class)).thenReturn(http);
		controller.forgotPassword(guilherme.getEmail());
		assertEquals("vraptor.email_not_found", result.included("error"));
	}

	@Test
	public void send_email_to_user_when_valid_email_is_passed() {
		SystemUser user = new MockUser();

		when(users.findByEmail(user.getEmail())).thenReturn(some(user));

		controller.forgotPassword(user.getEmail());
		assertTrue(mailer.sentEmailTo(user.getName(), user.getEmail()));
	}

	@Test(expected = ValidationException.class)
	public void redirect_to_login_page_if_invalid_newPasswordToken_is_passed() {
		when(users.findForEncryptedURL(null)).thenReturn(noUser());
		controller.resetPassword(null);
	}

	private Option<SystemUser> noUser() {
		return none();
	}

	@Test
	public void change_password_if_a_valid_newPasswordToken_is_passed() {
		SystemUser user = new MockUser();
		String newPasswordToken = user.getPassword()
				.generateEncryptedRecoveryText(user.getEmail());
		when(users.findForEncryptedURL(newPasswordToken))
				.thenReturn(some(user));

		controller.reassignPassword("someNewPassword", newPasswordToken);
		
		verify(auth).authenticate(user);

		assertEquals(Digester.encrypt("someNewPassword"), user.getPassword()
				.getPassword());
		assertNotSame(newPasswordToken, user.getPassword()
				.getLastEncryptedRecoveryURL());
	}
}
