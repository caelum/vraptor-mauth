package br.com.caelum.vraptor.mauth.user;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Keeps track of user navigation information such as his last access.
 */
@Embeddable
public class NavigationInfo {

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	@NotNull
	private DateTime lastAccessTime = new DateTime();

	public void ping() {
		lastAccessTime = new DateTime();
	}

	public int hoursAgo() {
		return new Period(lastAccessTime, new DateTime()).getHours();
	}

	public String toString() {
		return lastAccessTime.toString();
	}

}