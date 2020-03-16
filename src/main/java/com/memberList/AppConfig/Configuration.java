package com.memberList.AppConfig;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

	private static SessionFactory factory;
	private static Session session;

	public Session getSessionFactory() {
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();

		Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();

		factory = meta.getSessionFactoryBuilder().build();
		session = factory.openSession();
		return session;
	}

	public Transaction getTransaction() {
		Transaction t = session.beginTransaction();
		return t;
	}

	public boolean closeAll() {
		factory.close();
		session.close();
		return true;
	}

}
