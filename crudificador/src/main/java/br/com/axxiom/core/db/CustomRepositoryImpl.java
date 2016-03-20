package br.com.axxiom.core.db;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class CustomRepositoryImpl<T, P extends Serializable> extends SimpleJpaRepository<T, P> implements CustomRepository<T, P> {
    private EntityManager em;

    public CustomRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
    }
    //TODO Rever amanhã
//    @Override
//    public void refresh(T entity) {
//        em.refresh(entity);        
//    }
//
////	@Override
////	public void evict(T entity) {
////        Session session = em.unwrap(Session.class);
////        session.evict(entity);
////	}
}
