package br.com.caelum.vraptor.mauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Option;
import br.com.caelum.vraptor.ioc.Component;

@SuppressWarnings("unchecked")
@Component
public class CookieUser<T extends SystemUser> {

	private Option<T> user;

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	private final AuthUserRepository users;

	protected String getCookieName() {
		return "caelum.login.token";
	}

	public CookieUser(AuthUserRepository users,
			HttpServletRequest request, HttpServletResponse response) {
		this.users = users;
		this.request = request;
		this.response = response;
		Cookie cookie = findCookie();
		this.user = extractFrom(cookie);
	}

	public Option<T> getPossibleUser() {
		return user;
	}

	private Option<T> extractFrom( Cookie cookie) {

		if (cookie == null) {
			return Option.none();
		}

		return (Option<T>) users.accessed(cookie.getValue());

	}

	private Cookie findCookie() {
		for (Cookie cookie : this.getCookies()) {
			if (cookie.getName().equals(getCookieName())) {
				return cookie;
			}
		}
		return null;
	}

	private List<Cookie> getCookies() {
		if (request.getCookies() == null || request.getCookies().length==0) {
			return new ArrayList<Cookie>();
		}
		return Arrays.asList(request.getCookies());
	}

	public void login(Long id) {
		T user = (T) users.load(id);
		
		if (user == null)
			return;

		this.user = Option.some(user);

		Cookie cookie = new Cookie(getCookieName(), this.user.get().getToken());
		cookie.setMaxAge(days(7));
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	private int days(int i) {
		return i*24*60*60;
	}

	public void logout() {
		users.logout(user.getOrNull());

		this.user = Option.none();
		Cookie cookie = new Cookie(getCookieName(), "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

}