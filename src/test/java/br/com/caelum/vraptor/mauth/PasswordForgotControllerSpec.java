package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.Option.none;
import static br.com.caelum.vraptor.Option.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.simplemail.Mailer;
import br.com.caelum.vraptor.simplemail.template.TemplateMailer;
import br.com.caelum.vraptor.simplemail.testing.MockTemplateMailer;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.HttpResult;

@RunWith(MockitoJUnitRunner.class)
public class PasswordForgotControllerSpec {

	@Mock
	private AuthUserRepository users;
	private MockResult result;
	private PasswordForgotController controller;
	@Mock
	private HttpResult http;
	private TemplateMailer templates;
	@Mock
	private Mailer mailer;
	@Mock
	private Authenticator<SystemUser> auth;

	@Before
	public void before() {
		this.result = spy(new MockResult());
		this.templates = new MockTemplateMailer();
		this.controller = new PasswordForgotController(result, users, templates,
				auth, mailer);
	}

	@Test
	public void complain_if_user_email_is_not_found() throws Exception {
		when(users.findByEmail(null)).thenReturn(noUser());

		SystemUser guilherme = new MockUser();
		when(users.findByEmail(guilherme.getEmail())).thenReturn(noUser());

		when(result.use(HttpResult.class)).thenReturn(http);
		controller.forgotPassword(guilherme.getEmail());
		assertEquals("vraptor.email_not_found", result.included("error"));
	}

	@Test
	public void send_email_to_user_when_valid_email_is_passed() throws Exception {
		SystemUser user = new MockUser();

		when(users.findByEmail(user.getEmail())).thenReturn(some(user));

		controller.forgotPassword(user.getEmail());

		verify(mailer).send(argThat(emailTo(user.getName(), user.getEmail())));
	}

	@Test
	public void redirect_to_login_page_if_invalid_newPasswordToken_is_passed() {
		when(users.findForEncryptedURL(null)).thenReturn(noUser());
		controller.resetPassword(null);
		verify(result).redirectTo("/");
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

	private Option<SystemUser> noUser() {
		return none();
	}

	private Matcher<? extends Email> emailTo(final String name, final String email) {
		return new TypeSafeMatcher<Email>() {
			@Override
			public void describeTo(Description desc) {
				desc.appendText("Email to " + name + " <" + email + ">");
			}

			@Override
			public boolean matchesSafely(Email sent) {
				try {
					return sent.getToAddresses().contains(new InternetAddress(email, name));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
