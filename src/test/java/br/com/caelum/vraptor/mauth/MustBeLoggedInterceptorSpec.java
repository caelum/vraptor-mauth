package br.com.caelum.vraptor.mauth;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.HttpResult;
import br.com.caelum.vraptor.view.Results;

@RunWith(MockitoJUnitRunner.class)
public class MustBeLoggedInterceptorSpec {

	@Mock
	private PossibleUser loggedUser;
	@Mock
	private InterceptorStack stack;
	@Mock
	private ResourceMethod method;
	@Mock
	private HttpServletRequest request;
	@Mock
	private Object instance;
	@Mock
	private HttpResult mockHttp;
	private final MockResult result = spy(new MockResult());
	private MustBeLoggedInterceptor interceptor;

	@Before
	public void config() {
		this.interceptor = new MustBeLoggedInterceptor(loggedUser, result, request);
	}

	@Test
	public void continueIfUserIsLogged() {
		StringBuffer url = new StringBuffer("http://localhost:8080/qualquerurl");
		when(loggedUser.isSignedIn()).thenReturn(true);
		when(request.getRequestURL()).thenReturn(url);

		interceptor.intercept(stack, method, instance);

		verify(stack).next(method, instance);
	}

	@Test
	public void redirect_to_login_if_user_is_not_logged() {
		StringBuffer url = new StringBuffer("http://localhost:8080/qualquerurl");
		when(request.getRequestURL()).thenReturn(url);
		interceptor.intercept(stack, method, instance);

		verify(result).redirectTo("/?urlAfterLogin=" + url);
	}

	@Test
	public void send_401_if_request_is_ajax() {
		when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
		when(result.use(Results.http())).thenReturn(mockHttp);

		interceptor.intercept(stack, method, instance);

		verify(mockHttp).sendError(401);
	}
}
