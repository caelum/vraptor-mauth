package br.com.caelum.vraptor.mauth;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.mauth.PossibleUser;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Intercepts
public class LoggedUserInterceptor  implements Interceptor{


	private PossibleUser user;
	private final HttpServletRequest request;
	
	public LoggedUserInterceptor(PossibleUser user, HttpServletRequest request) {
		this.user = user;
		this.request = request;
	}

	@Override
	public boolean accepts(ResourceMethod arg0) {
		return true;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object instance) throws InterceptionException {
		if(user.isSignedIn())
			request.setAttribute("currentUser", user.get());
		stack.next(method, instance);
	}
	
}
