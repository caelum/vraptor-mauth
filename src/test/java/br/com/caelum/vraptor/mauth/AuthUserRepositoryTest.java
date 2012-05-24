package br.com.caelum.vraptor.mauth;

import static br.com.caelum.vraptor.Option.none;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.com.caelum.gnarus.utilMovePlease.auth.OurAuthConfiguration;
import br.com.caelum.gnarus.utilMovePlease.auth.User;
import br.com.caelum.vraptor.Option;

public class AuthUserRepositoryTest {
	@Test
	public void find_user_for_last_encrypted_string() {

		String firstGenerated = aUser.getPassword().generateEncryptedRecoveryText(aUser.getEmail());

		session.beginTransaction();
		users().update(aUser);
		session.getTransaction().commit();
		session.evict(aUser);

		User firstUser = users().findForEncryptedURL(firstGenerated).get();
		assertEquals(aUser.getId(), firstUser.getId());
	}

	@Test
	public void find_None_for_unknow_encrypted_string() {
		aUser.getPassword().generateEncryptedRecoveryText(aUser.getEmail());
		assertEquals(none(), users().findForEncryptedURL("12890371238172381"));
	}
	private static final String TOKEN_FOR_USER = "token_for_user";
	@Mock
	private Transaction tx;
	private AuthConfiguration config;
	private AuthUserRepository repository;
	private User userWithToken = new User();
	private User userWithoutToken;
	
	@Before
	public void setUp() {
		config = new OurAuthConfiguration();
		repository = new AuthUserRepository(session, tx, config);
		
		userWithToken.setToken(TOKEN_FOR_USER);
		
		userWithoutToken = new User();
		
		session.beginTransaction();
		session.save(userWithToken);
		session.save(userWithoutToken);
		session.getTransaction().commit();
		session.evict(userWithToken);
		
		Mockito.spy(userWithToken);
		
	}

	@Test
	public void shouldFindUserWithTokenAndAccessNavigationInfo() {
		Option<SystemUser> option = repository.accessed(TOKEN_FOR_USER);
		User found = (User) option.get();
		
		assertEquals(TOKEN_FOR_USER, found.getToken());
		verify(userWithToken).getNavigationInfo();
	}
	
	@Test(expected=IllegalStateException.class)
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
		
		assertNotNull(userWithToken.getToken());
	}
	
	

}
