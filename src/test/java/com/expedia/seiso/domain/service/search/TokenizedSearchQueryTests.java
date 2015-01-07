package com.expedia.seiso.domain.service.search;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

import com.expedia.seiso.domain.service.search.SearchQuery;

public class TokenizedSearchQueryTests {

	@Test
	public void testSetTokens() {
		SearchQuery tokenizedSearchQuery = new SearchQuery();

		Set<String> expected = new TreeSet<String>();
		expected.add("this");
		expected.add("is");
		expected.add("a");
		expected.add("test");
		tokenizedSearchQuery.setTokens(expected);
		Set<String> actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetNullTokens() {
		SearchQuery tokenizedSearchQuery = new SearchQuery();

		Set<String> expected = new TreeSet<String>();
		tokenizedSearchQuery.setTokens(null);
		Set<String> actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testClearTokens() {
		SearchQuery tokenizedSearchQuery = new SearchQuery();

		Set<String> expected = new TreeSet<String>();
		expected.add("this");
		expected.add("is");
		expected.add("a");
		expected.add("test");
		tokenizedSearchQuery.setTokens(expected);
		Set<String> actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);

		tokenizedSearchQuery.clearTokens();

		expected.clear();
		actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);
	}

}
