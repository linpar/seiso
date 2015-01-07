package com.expedia.seiso.domain.service.search;

import javax.validation.ConstraintValidatorContext;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.domain.service.search.Validator;

public class ValidatorTests {
	private static String goodQuery;
	private static String queryPassesQueryPatternButNotTokensPattern;
	private static String queryFailsQueryPattern;

	private static @Mock ConstraintValidatorContext constraintValidatorContext;

	@BeforeClass
	public static void setup() {
		MockitoAnnotations.initMocks(ValidatorTests.class);

		ValidatorTests.goodQuery = "good query";
		ValidatorTests.queryFailsQueryPattern = "a";
		ValidatorTests.queryPassesQueryPatternButNotTokensPattern = "a b c";
	}

	// TODO improve tests to inspect constraints as well as boolean result

	@Test
	public void testNull() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		SearchQuery tokenizedSearchQuery = null;
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorTests.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testEmpty() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		SearchQuery tokenizedSearchQuery = new SearchQuery();
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorTests.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGoodQueryAndTokens() throws InstantiationException, IllegalAccessException {
		boolean expected = true;

		SearchQuery tokenizedSearchQuery = new SearchQuery(ValidatorTests.goodQuery);
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorTests.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testBadQuery() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		SearchQuery tokenizedSearchQuery = new SearchQuery(ValidatorTests.queryFailsQueryPattern);
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorTests.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testBadTokens() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		SearchQuery tokenizedSearchQuery = new SearchQuery(
				ValidatorTests.queryPassesQueryPatternButNotTokensPattern);
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorTests.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

}
