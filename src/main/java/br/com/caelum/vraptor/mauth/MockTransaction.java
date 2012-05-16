package br.com.caelum.vraptor.mauth;

public class MockTransaction extends Transaction {

	public MockTransaction() {
		super(null);
	}

	@Override
	public void execute(Runnable code) {
		code.run();
	}
}
