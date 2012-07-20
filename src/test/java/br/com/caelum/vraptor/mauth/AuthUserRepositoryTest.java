package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.Option.none;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Option;

@RunWith(MockitoJUnitRunner.class)
public class AuthUserRepositoryTest extends DatabaseTest{

	private static final String TOKEN_FOR_USER = "token_for_user";

	private Transaction tx = new MockTransaction();
	private AuthConfiguration config;
	private AuthUserRepository repository;
	private User userWithToken;
	private User userWithoutToken;
	private User aUser;
	
	@Before
	public void setUp() {
		
		config = new OurAuthConfiguration();
		repository = new AuthUserRepository(session, tx, config);
		
		Password pass = new Password();
		String email = "mozart@gmail.com";
		pass.setPassword(email);
		
		aUser = new User("mozart",email,pass);
		users().save(aUser);
		
		userWithToken = new User();
		userWithToken.setToken(TOKEN_FOR_USER);
		userWithoutToken = new User();
		
		users().save(userWithToken);
		users().save(userWithoutToken);

		session.evict(userWithToken);
	}

	@Test
	public void find_user_for_last_encrypted_string() {

		String firstGenerated = aUser.getPassword().generateEncryptedRecoveryText("bla");
		users().update(aUser);
		
		User firstUser = users().findForEncryptedURL(firstGenerated).get();
		assertEquals(aUser.getId(), firstUser.getId());

	}

	@Test
	public void find_None_for_unknow_encrypted_string() {
		aUser.getPassword().generateEncryptedRecoveryText(aUser.getEmail());
		assertEquals(none(), users().findForEncryptedURL("12890371238172381"));
	}
	
	@Test
	public void shouldFindUserWithTokenAndAccessNavigationInfo() {
		Option<SystemUser> option = repository.accessed(TOKEN_FOR_USER);
		User found = (User) option.get();
		
		assertEquals(TOKEN_FOR_USER, found.getToken());
		assertEquals(userWithToken, found);
	}
	
	@Test
	public void shouldReturnNoneOptionIfNoUserWithTokenAvaliable() {
		Option<SystemUser> option = repository.accessed("some_token");
		
		assertEquals(Option.none(), option);
	}
	
	@Test
	public void shouldGenerateTokenWhenLogUser() throws Exception {
		SystemUser loaded = repository.load(userWithoutToken);
		
		assertNotNull(loaded.getToken());
	}
	
	@Test
	public void shouldSetUsersTokenToNullWhenLoggingOut() throws Exception {
		repository.logout(userWithToken);
		
		assertNull(userWithToken.getToken());
	}
	
	

}
