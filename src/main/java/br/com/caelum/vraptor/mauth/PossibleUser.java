package br.com.caelum.vraptor.mauth;

/**
 * An possible logged user
 * 
 * @author guilherme silveira
 */
public interface PossibleUser {

	boolean isSignedIn();

	SystemUser get();

}
