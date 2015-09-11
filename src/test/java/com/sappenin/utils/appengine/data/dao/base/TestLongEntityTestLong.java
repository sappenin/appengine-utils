package com.sappenin.utils.appengine.data.dao.base;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test classes to validate abstract testers.
 */
public class TestLongEntityTestLong extends AbstractObjectifyLongDaoTester<TestLongEntity>
{
	private TestLongEntityDao dao;

	private DateTime nowForTest;

	@Override
	protected void setUpAbstractDaoTester()
	{
		dao = new TestLongEntityDao();
		nowForTest = DateTime.now(DateTimeZone.UTC);
	}

	@Override
	protected TestLongEntityDao getDao()
	{
		return dao;
	}

	@Override
	protected TestLongEntity getEmptyTestEntityWithNoKey()
	{
		return new TestLongEntity();
	}

	@Override
	protected TestLongEntity getFullyPopulatedEntity()
	{
		TestLongEntity entity = this.getEmptyTestEntityWithNoKey();

		entity.setCreationDateTime(nowForTest);

		entity.setTestValue("testValue");
		return entity;
	}

	@Override
	protected void doFullEntityAssertions(final TestLongEntity entityLoadedFromDataStore)
	{
		assertThat(entityLoadedFromDataStore.getKey(), is(notNullValue()));

		this.compareTwoDatesWithMillisecondImprecision(entityLoadedFromDataStore.getCreationDateTime(), nowForTest);
		this.compareTwoDatesWithMillisecondImprecision(entityLoadedFromDataStore.getUpdateDateTime(), nowForTest);

		assertThat(entityLoadedFromDataStore.getTestValue(), is("testValue"));
	}

	@Override
	protected void assertThatLoadedEntityWasUpdatedProperly(final TestLongEntity entityLoadedFromDataStore)
	{
		assertThat(entityLoadedFromDataStore.getTestValue(), is("testValue2"));
	}

	@Override
	protected void changeSomethingMinorOnSuppliedEntity(final TestLongEntity entityThatWillBeSaved)
	{
		entityThatWillBeSaved.setTestValue("testValue2");
	}

	/**
	 * An extension of {@link AbstractObjectifyLongDao} for testing purposes.
	 */
	public static final class TestLongEntityDao extends AbstractObjectifyLongDao<TestLongEntity>
	{
	}

}
