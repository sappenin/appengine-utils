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
public class TestLongEntityTest extends AbstractObjectifyLongDaoTester<TestLongEntity>
{
	private TestLongEntityDao dao;

	private DateTime nowForTest;

	@Override
	protected void setUpAbstractDaoTester()
	{
		ObjectifyService.factory().getTranslators().add(new DateTimeZoneTranslatorFactory());
		ObjectifyService.factory().getTranslators().add(new ReadableInstantTranslatorFactory());

		ObjectifyService.factory().register(TestLongEntity.class);

		dao = new TestLongEntityDao();
		nowForTest = DateTime.now(DateTimeZone.UTC);
	}

	@Override
	protected TestLongEntityDao getDao()
	{
		return dao;
	}

	@Override
	protected TestLongEntity getEmptyTestEntityWithNoKey() throws Exception
	{
		return new TestLongEntity();
	}

	@Override
	protected TestLongEntity getEmptyTestEntityWithKey() throws Exception
	{
		Key<TestLongEntity> key = Key.create(TestLongEntity.class, 123L);
		return new TestLongEntity(key);
	}

	@Override
	protected TestLongEntity getFullyPopulatedEntity() throws Exception
	{
		TestLongEntity entity = this.getEmptyTestEntityWithKey();

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
