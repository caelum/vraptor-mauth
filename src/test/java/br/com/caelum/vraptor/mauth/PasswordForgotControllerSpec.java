package br.com.caelum.vraptor.mauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.view.HttpResult;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User;
import com.sun.tools.javac.comp.Env;

public class PasswordForgotControllerSpec {

	private @Mock
	AuthUserRepository users;
	private @Mock
	Env env;
	private MockResult result;
	private MockValidator validator;
	private PasswordForgotController controller;
	private @Mock
	HttpResult http;
	private @Mock
	Transaction transaction;

	@Before
	public void before() {
		this.result = spy(new MockResult());
		this.validator = spy(new MockValidator());
		when(env.host()).thenReturn("http://link");
		this.controller = new PasswordForgotController(result, users,
				transaction, validator);
	}

	@Test
	public void complain_if_user_email_is_not_found() {
		when(users.findByEmail(null)).thenReturn(noUser());

		User guilherme = new UserBuilder().create();
		when(users.findByEmail(guilherme.getEmail())).thenReturn(noUser());

		when(result.use(HttpResult.class)).thenReturn(http);
		controller.forgotPassword(guilherme.getEmail());
		verify(http).body("???gnarus.email.notfound???");
	}

	@Test
	public void send_email_to_user_when_valid_email_is_passed() {
		User user = new UserBuilder().create();

		when(users.findByEmail(user.getEmail())).thenReturn(some(user));

		controller.forgotPassword(user.getEmail());
		verify(mails).dispatch(any(PasswordForgotMail.class));
	}

	@Test(expected = ValidationException.class)
	public void redirect_to_login_page_if_invalid_newPasswordToken_is_passed() {
		when(users.findForEncryptedURL(null)).thenReturn(noUser());
		controller.resetPassword(null);
	}

	private Option<User> noUser() {
		return none();
	}

	@Test
	public void change_password_if_a_valid_newPasswordToken_is_passed() {
		User user = new UserBuilder().create();
		String newPasswordToken = user.getPassword()
				.generateEncryptedRecoveryText(user.getEmail());
		when(users.findForEncryptedURL(newPasswordToken))
				.thenReturn(some(user));

		controller.reassignPassword("someNewPassword", newPasswordToken);

		assertEquals(Digester.encrypt("someNewPassword"), user.getPassword()
				.getPassword());
		assertNotSame(newPasswordToken, user.getPassword()
				.getLastEncryptedRecoveryURL());
	}
}
