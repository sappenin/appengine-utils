package com.sappenin.utils.appengine.data.dao.base;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.opt.joda.DateTimeZoneTranslatorFactory;
import com.googlecode.objectify.impl.translate.opt.joda.ReadableInstantTranslatorFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test classes to validate abstract testers.
 */
public class TestStringEntityTest extends AbstractObjectifyStringDaoTester<TestStringEntity>
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
	protected TestStringEntity getEmptyTestEntityWithNoKey() throws Exception
	{
		return new TestStringEntity();
	}

	@Override
	protected TestStringEntity getEmptyTestEntityWithKey() throws Exception
	{
		Key<TestStringEntity> key = Key.create(TestStringEntity.class, "testKey");
		return new TestStringEntity(key);
	}

	@Override
	protected TestStringEntity getFullyPopulatedEntity() throws Exception
	{
		TestStringEntity entity = this.getEmptyTestEntityWithKey();

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
