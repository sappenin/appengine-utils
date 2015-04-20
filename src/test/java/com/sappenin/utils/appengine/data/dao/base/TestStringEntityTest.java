package com.sappenin.utils.appengine.data.dao.base;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test classes to validate abstract testers.
 */
public class TestStringEntityTest extends AbstractObjectifyStringObjectifyDaoTester<TestStringEntity>
{
	private TestStringEntityDao dao;

	private DateTime nowForTest;

	@Override
	protected void setUpAbstractDaoTester()
	{
		dao = new TestStringEntityDao();
		nowForTest = DateTime.now(DateTimeZone.UTC);
	}

	@Override
	protected TestStringEntityDao getDao()
	{
		return dao;
	}

	@Override
	protected TestStringEntity getEmptyTestEntityWithNoKey()
	{
		return new TestStringEntity();
	}

	@Override
	protected TestStringEntity getFullyPopulatedEntity()
	{
		TestStringEntity entity = this.getEmptyTestEntityWithNoKey();

		entity.setCreationDateTime(nowForTest);

		entity.setTestValue("testValue");
		return entity;
	}

	@Override
	protected void doFullEntityAssertions(final TestStringEntity entityLoadedFromDataStore)
	{
		assertThat(entityLoadedFromDataStore.getKey(), is(notNullValue()));

		this.compareTwoDatesWithMillisecondImprecision(entityLoadedFromDataStore.getCreationDateTime(), nowForTest);
		this.compareTwoDatesWithMillisecondImprecision(entityLoadedFromDataStore.getUpdateDateTime(), nowForTest);

		assertThat(entityLoadedFromDataStore.getTestValue(), is("testValue"));
	}

	@Override
	protected void assertThatLoadedEntityWasUpdatedProperly(final TestStringEntity entityLoadedFromDataStore)
	{
		assertThat(entityLoadedFromDataStore.getTestValue(), is("testValue2"));
	}

	@Override
	protected void changeSomethingMinorOnSuppliedEntity(final TestStringEntity entityThatWillBeSaved)
	{
		entityThatWillBeSaved.setTestValue("testValue2");
	}

	public static final class TestStringEntityDao extends AbstractObjectifyStringDao<TestStringEntity>
	{

	}

}
