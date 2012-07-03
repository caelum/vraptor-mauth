package br.com.caelum.vraptor.mauth;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

public class DatabaseTest {
	
	private static final SessionFactory factory;
	static {
		Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
		cfg.addAnnotatedClass(User.class);
		factory = cfg.buildSessionFactory();
	}

	protected Session session;
	
	protected final UserDAO users() {
		return new UserDAO(session);
	} 
	
	@Before
	public void beforeDatabase() {
		session = factory.openSession();
		session.beginTransaction();
	}

	@After
	public void afterDatabase() {
		if (session.getTransaction().isActive()) {
			session.getTransaction().rollback();
		}
		session.close();
	}
}
