package br.com.caelum.vraptor.mauth;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


@Embeddable
public class Password {
	@NotEmpty
	@Length(min = 6)
	private String password;

	@Column(unique = true)
	private String lastEncryptedRecoveryURL;

	/** hibernate eyes only */
	public Password() {
	}

	/** vraptor eyes only, remove if paranamer gets installed into sbt */
	@Deprecated
	public void setPassword(String password) {
		this.password = password;
	}

	public void changeTo(String newPassword) {
		if (newPassword == null || newPassword.isEmpty()) {
			throw new IllegalArgumentException("password can not be empty");
		}
		password = Digester.encrypt(newPassword);
	}

	public boolean matchesWith(String passwordToMatch) {
		return passwordToMatch != null
				&& password.equals(Digester.encrypt(passwordToMatch));
	}

	public String generateEncryptedRecoveryText(String email) {
		return lastEncryptedRecoveryURL = Digester.encrypt(System.nanoTime()
				+ email);
	}

	public String getLastEncryptedRecoveryURL() {
		return lastEncryptedRecoveryURL;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean isDefined() {
		return this.password != null;
	}

}