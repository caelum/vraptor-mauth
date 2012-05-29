package br.com.caelum.vraptor.mauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.Results;

@Intercepts(before = InstantiateInterceptor.class)
public class MustBeLoggedInterceptor implements Interceptor {

	private final PossibleUser user;
	private final Result result;
	private final HttpServletRequest request;

	public MustBeLoggedInterceptor(PossibleUser user, Result result,
			HttpServletRequest request) {
		this.user = user;
		this.result = result;
		this.request = request;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return !method.getResource().getType().isAnnotationPresent(Open.class)
				&& !method.containsAnnotation(Open.class);
	}

	private String getReferer() {
		if (request.getQueryString() != null) {
			return request.getRequestURL() + "?" + request.getQueryString();
		} else {
			return request.getRequestURL().toString();
		}
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object instance) throws InterceptionException {

		if (user.isSignedIn()) {
			request.setAttribute("currentUser", user.get());
			stack.next(method, instance);
		} else if (isAjaxRequest()) {
			result.use(Results.http()).sendError(
					HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			result.forwardTo("/?urlAfterLogin=" + getReferer());
		}
	}

	private boolean isAjaxRequest() {
		return request.getHeader("X-Requested-With") != null;
	}
}