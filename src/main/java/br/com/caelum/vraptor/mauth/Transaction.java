package br.com.caelum.vraptor.mauth;

import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class Transaction {

	private final Session session;

	public Transaction(Session session) {
		this.session = session;
	}

	public void execute(Runnable code) {
		if (session.getTransaction().isActive()) {
			code.run();
		} else {
			org.hibernate.Transaction tx = session.beginTransaction();
			try {
				code.run();
				tx.commit();
			} finally {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
		}
	}
}
