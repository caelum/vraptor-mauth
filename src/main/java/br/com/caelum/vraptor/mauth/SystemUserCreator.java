package br.com.caelum.vraptor.mauth;



/**
 * A factory that creates users based on a facebook profile or connects a logged
 * user to a profile.
 * 
 * @author guilherme silveira
 * @author hugo roque
 */
public interface SystemUserCreator<T extends SystemUser> {

	/**
	 * Creates an instance of this system's user and save it.
	 * @param profile
	 * @return
	 */
	T create(String profile);

	/**
	 * Connects this user to this facebook profile updating its data.
	 * @param user
	 * @param profile
	 */
	void connect(T user, String profile);

}
