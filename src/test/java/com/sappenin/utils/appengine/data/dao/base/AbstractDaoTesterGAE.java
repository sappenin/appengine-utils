package com.sappenin.utils.appengine.data.dao.base;

import com.google.appengine.api.datastore.Cursor;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.utils.appengine.base.GaeTestHarnessInitializationAdapter;
import com.sappenin.utils.appengine.data.dao.Dao;
import com.sappenin.utils.appengine.data.dao.ObjectifyDao;
import com.sappenin.utils.appengine.data.dao.base.TestLongEntityTest.TestLongEntityDao;
import com.sappenin.utils.appengine.data.model.GaeTypedEntity;
import com.sappenin.utils.appengine.data.model.base.AbstractEntity;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Provides common test functionality for DAO's, such as save and delete functions.
 *
 * @param <{@link T} extends {@link AbstractEntity}>
 *
 * @author David Fuelling
 */
public abstract class AbstractDaoTesterGAE<T extends AbstractEntity & GaeTypedEntity<T>>
		extends GaeTestHarnessInitializationAdapter
{

	private final AbstractObjectifyDao<T> impl = (AbstractObjectifyDao) this.getDao();

	@Before
	public void setUpAbstractDaoTesterInternal() throws Exception
	{
		this.setUpAbstractDaoTester();
	}

	/**
	 * Implement this with custom test functionality.
	 */
	protected abstract void setUpAbstractDaoTester();

	// ///////////////////////////////////////////////////
	// COMMON TEST HARNESS
	// ///////////////////////////////////////////////////

	@Test(expected = NullPointerException.class)
	public void TestSaveNullEntity()
	{
		final T entity = null;
		this.getDao().save(entity);
	}

	/**
	 * Tests a fully-created entity from the Datastore.
	 *
	 * @return
	 */
	@Test
	public void TestSaveWithNoFieldsPopulated() throws Exception
	{
		final T entity = this.getEmptyTestEntityForValidSave();
		this.getDao().save(entity);

		// Do Assertions
		this.doCommonAssertions(entity);
	}

	/**
	 * Tests what happens when a "dao#save"  is called on an entity with no id.
	 */
	@Test
	public abstract void TestNonIdempotentSave() throws Exception;

	/**
	 * Tests what happens when a "dao#save"  is called on an entity with an id.
	 */
	@Test
	public abstract void TestIdempotentSave() throws Exception;

	/**
	 * Tests what happens when the "dao#create"  is called on an entity with no id.
	 */
	@Test
	public abstract void TestNonIdempotentCreate() throws Exception;

	/**
	 * Tests what happens when a "dao#create"  is called on an entity with an id.
	 */
	@Test
	public abstract void TestIdempotentCreate() throws Exception;

	/**
	 * Helper method to allow sub-classes to provide a valid entity for saving.
	 *
	 * @return
	 */
	protected abstract T getEmptyTestEntityForValidSave() throws Exception;

	/**
	 * Helper method to allow sub-classes to provide a valid entity for saving.
	 *
	 * @return
	 */
	protected abstract T getEmptyTestEntityForValidCreate() throws Exception;

	/**
	 * Tests a fully-created entity from the Datastore.
	 *
	 * @return
	 */
	@Test
	public void TestSaveWithAllFieldsPopulated() throws Exception
	{
		final T entity = this.getFullyPopulatedEntity();
		this.getDao().save(entity);

		// Do Assertions
		this.doCommonAssertions(entity);

		this.doFullEntityAssertions(entity);
	}

	@Test
	public void TestSaveAfterUpdate() throws Exception
	{
		final T entity = this.getFullyPopulatedEntity();
		this.getDao().save(entity);
		this.changeSomethingMinorOnSuppliedEntity(entity);
		this.getDao().save(entity);

		// Do Assertions
		this.doCommonAssertions(entity);

		// We use the 'entity' here instead of the updatedEntity because the
		// entity will look different when returned from the datastore versus
		// when it is just updated in Java.
		this.assertThatLoadedEntityWasUpdatedProperly(entity);
	}

	/**
	 * Ensure that the Key of the Entity retrieved from the Datastore matches the Key of the Entity that was put there.
	 */
	@Test
	public void TestFindByTypedPK() throws Exception
	{
		final T entity = getEmptyTestEntityForValidSave();
		this.getDao().save(entity);

		final ObjectifyDao<T> ofyDao = (ObjectifyDao<T>) this.getDao();

		final Key<T> typedKey = entity.getTypedKey();
		final Optional<T> optLoadedEntity = ofyDao.findByTypedKey(typedKey);

		assertNotNull(optLoadedEntity);
		assertTrue(optLoadedEntity.isPresent());

		T entityLoadedFromDataStore = optLoadedEntity.get();
		assertEquals(entity.getKey(), entityLoadedFromDataStore.getKey());
		assertEquals(entity.getTypedKey(), entityLoadedFromDataStore.getTypedKey());
		assertEquals(entity, entityLoadedFromDataStore);

	}

	@Test
	public void TestGetDAO() throws Exception
	{
		assertNotNull(this.getDao());
	}

	/////////////////////////////
	// AbstractObjectifyDao Private Helpers
	/////////////////////////////

	// #AdjustLimit

	@Test
	public void TestAdjustLimit() throws Exception
	{
		final AbstractObjectifyDao<T> impl = (AbstractObjectifyDao) this.getDao();

		assertThat(impl.adjustLimit(-1), is(10));
		assertThat(impl.adjustLimit(-0), is(10));
		assertThat(impl.adjustLimit(5), is(5));
		assertThat(impl.adjustLimit(10), is(10));
		assertThat(impl.adjustLimit(15), is(15));
	}

	// #MassageQuery

	@Test(expected = NullPointerException.class)
	public void TestMassageQuery_NullQuery() throws Exception
	{
		final Query<T> finalizedQuery = null;
		impl.massageQuery(finalizedQuery, Cursor.fromWebSafeString(""), 10);
	}

	//	Other MassageQuery tests are in the String and Long DAO Tester

	// #existsInDatastore

	@Test(expected = NullPointerException.class)
	public void TestExistsInDatastore_NullInput() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();
		impl.existsInDatastore(null);
	}

	@Test
	public void TestExistsInDatastore() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		final Key<TestLongEntity> notFoundKey = Key.create(TestLongEntity.class, 2L);
		final Key<TestLongEntity> foundKey = Key.create(TestLongEntity.class, 1L);
		final TestLongEntity entity = new TestLongEntity(foundKey);
		impl.save(entity);

		assertThat(impl.existsInDatastore(foundKey), is(true));
		assertThat(impl.existsInDatastore(notFoundKey), is(false));
	}

	// #existsInDatastoreConsistent
	// See Long and String Dao tests

	// #assembleResultWithCursor

	// See AbstractObjectifyLongDaoTester and AbstractObjectifyStringDaoTester

	// #determinePageIndices

	// //////////////////////////////////////////////////////////
	// ABSTRACT FUNCTIONS
	// //////////////////////////////////////////////////////////

	/**
	 * Returns the Entity Manager for this AbstractObjectifyLongDao Testor.
	 *
	 * @return
	 */
	protected abstract Dao<T> getDao();

	/**
	 * Get an empty Entity with no Key.
	 *
	 * @return
	 */
	protected abstract T getEmptyTestEntityWithNoKey() throws Exception;

	/**
	 * Get an empty Entity with a Key.
	 *
	 * @return
	 */
	protected abstract T getEmptyTestEntityWithKey() throws Exception;

	/**
	 * Returns a Test entity that has all fields populated, including its Key.
	 *
	 * @return
	 */
	protected abstract T getFullyPopulatedEntity() throws Exception;

	/**
	 * Makes one or more changes (typically minor) to an entity in order to support the update() test.
	 *
	 * @param entityThatWillBeSaved
	 */
	protected abstract void changeSomethingMinorOnSuppliedEntity(final T entityThatWillBeSaved);

	/**
	 * Implementations of this function will receive an entity that has been updated in the datastore by a prior
	 * function, and then re-loaded from the Datastore. The test sub-class should check that the entity is not null,
	 * and
	 * check that any changes have occurred properly. Since the sub-class author will also be implementing the {@link
	 * #changeSomethingMinorOnSuppliedEntity} function, the author will know what to expect of the Entity passed into
	 * this function.
	 *
	 * @param entityLoadedFromDataStore The Entity has loaded from the DataStore/
	 */
	protected abstract void assertThatLoadedEntityWasUpdatedProperly(final T entityLoadedFromDataStore);

	/**
	 * Test the fully-populated Entity returned from the ds via a find-by after creating the Full Entity.
	 *
	 * @param entityLoadedFromDataStore The Entity loaded via findByPK after a creation.
	 */
	protected abstract void doFullEntityAssertions(final T entityLoadedFromDataStore);

	// //////////////////////////////////////////////////
	// HELPER FUNCTIONS
	// //////////////////////////////////////////////////

	/**
	 * Helper method to compare two objects of type {@link DateTime} without taking into account their milliseconds
	 * values. This is required for App Engine unit test coverage because often two DateTime objects will have slightly
	 * different millisecond values depending on how quickly the processor can process the unit tests. This is
	 * ultimately a bug in the way the local dev server loads DateTime values from the local datastore, but something I
	 * haven't been able to nail down.
	 *
	 * @param thisDate
	 * @param thatDate
	 */
	protected void compareTwoDatesWithMillisecondImprecision(final DateTime thisDate, final DateTime thatDate)
	{
		assertEquals(thisDate.getYear(), thatDate.getYear());
		assertEquals(thisDate.getMonthOfYear(), thatDate.getMonthOfYear());
		assertEquals(thisDate.getDayOfMonth(), thatDate.getDayOfMonth());
		assertEquals(thisDate.getHourOfDay(), thatDate.getHourOfDay());
		assertEquals(thisDate.getMinuteOfHour(), thatDate.getMinuteOfHour());
		assertEquals(thisDate.getSecondOfMinute(), thatDate.getSecondOfMinute());
	}

	/**
	 * A set of common assertions that can be performed after any given datastore test.
	 *
	 * @param entity
	 */
	protected void doCommonAssertions(final T entity) throws Exception
	{
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getKey(), is(notNullValue()));
		assertThat(entity.getId(), is(notNullValue()));
	}

}
