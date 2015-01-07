package com.expedia.seiso.domain.service.search;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.expedia.seiso.domain.service.search.QueryFactory;

public class QueryFactoryTests {
	private Object[] createQueryArguments;

	@Mock
	EntityManager entityManager;
	@Mock
	Query query;

	private void setUpMocks() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(this.entityManager.createQuery(Mockito.anyString())).thenAnswer(new Answer<Query>() {

			@Override
			public Query answer(InvocationOnMock invocation) throws Throwable {
				createQueryArguments = invocation.getArguments();

				return query;
			}

		});
	}

	@Test
	public void testBuildQuery() {
		this.setUpMocks();

		QueryFactory queryFactory = new QueryFactory();
		String entityName = "myTableName";

		Set<String> fieldNames = new LinkedHashSet<String>();
		fieldNames.add("fieldName1");
		fieldNames.add("fieldName2");

		Set<String> searchTokens = new LinkedHashSet<String>();
		searchTokens.add("searchToken1");
		searchTokens.add("searchToken2");

		queryFactory.buildQuery(entityName, this.entityManager, fieldNames, searchTokens);

		String expected = "from myTableName m where m.fieldName1 like :param0 or m.fieldName1 like :param1 or m.fieldName2 like :param0 or m.fieldName2 like :param1";
		String actual = (String) this.createQueryArguments[0];
		Assert.assertEquals(expected.trim(), actual.trim());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyFieldNames() {
		this.setUpMocks();

		QueryFactory queryFactory = new QueryFactory();
		String entityName = "myTableName";

		Set<String> fieldNames = new LinkedHashSet<String>();

		Set<String> searchTokens = new LinkedHashSet<String>();
		searchTokens.add("searchToken1");
		searchTokens.add("searchToken2");

		queryFactory.buildQuery(entityName, this.entityManager, fieldNames, searchTokens);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyTokens() {
		this.setUpMocks();

		QueryFactory queryFactory = new QueryFactory();
		String entityName = "myTableName";

		Set<String> fieldNames = new LinkedHashSet<String>();
		fieldNames.add("fieldName1");
		fieldNames.add("fieldName2");

		Set<String> searchTokens = new LinkedHashSet<String>();

		queryFactory.buildQuery(entityName, this.entityManager, fieldNames, searchTokens);
	}

}
