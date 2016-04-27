package com.expedia.seiso.web.eventhandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RestCallListener implements HandlerInterceptor  {

	@Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		Session session = this.sessionFactory.withOptions().interceptor(new NodeAttributeChangeLogger()).openSession();		
		System.out.println("Interceptor registered");
		return true;
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
