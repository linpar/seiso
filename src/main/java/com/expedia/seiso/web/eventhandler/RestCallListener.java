package com.expedia.seiso.web.eventhandler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.expedia.seiso.domain.entity.Node;


@Service
@Transactional
public class RestCallListener implements HandlerInterceptor  {

	@Autowired
	EntityManager entityManager;
	
	//@Autowired
	//SessionFactory sessionFactory;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		invokeInterceptor();
		return true;
	}
	
	private void invokeInterceptor(){
		//EntityManager entityManager = jpa.getEntityManagerByManagedType(Node.class);
		Session session = entityManager.unwrap(org.hibernate.Session.class);
		session.sessionWithOptions().interceptor((new NodeAttributeChangeLogger()));
		System.out.println("Interceptor registered");
	}
	
	@Override
	public void postHandle(	HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {
	}
} 
