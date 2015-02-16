package com.sappenin.utils.appengine.data.dao.base;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.utils.appengine.data.dao.ObjectifyStringDao;
import com.sappenin.utils.appengine.data.dao.base.TestLongEntityTest.TestLongEntityDao;
import com.sappenin.utils.appengine.data.dao.base.TestStringEntityTest.TestStringEntityDao;
import com.sappenin.utils.appengine.data.model.GaeTypedEntity;
import com.sappenin.utils.appengine.data.model.ResultWithCursor;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyStringEntity;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Provides common functionality for base DAO's, such as create, update, and delete functions.
 *
 * @param <{@link T} extends {@link BaseModelString}>
 *
 * @author David Fuelling
 */
public abstract class AbstractObjectifyStringDaoTester<T extends AbstractObjectifyStringEntity<T> & GaeTypedEntity<T>>
		extends AbstractDaoTesterGAE<T>
{
	/**
	 * Returns the Dao for this AbstractObjectifyStringDao Testor.
	 *
	 * @return
	 */
	@Override
	protected abstract ObjectifyStringDao<T> getDao();

	/**
	 * Tests what happens when a "dao#save"  is called on an entity with no id.
	 */
	@Test(expected = IllegalArgumentException.class)
	@Override
	public void TestNonIdempotentSave() throws Exception
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().save(entity);
	}

	/**
	 * Tests what happens when a "dao#save"  is called on an entity with an id.
	 */
	@Test
	@Override
	public void TestIdempotentSave() throws Exception
	{
		final T entity = getEmptyTestEntityWithKey();
		this.getDao().save(entity);
	}

	/**
	 * Tests what happens when the "dao#create"  is called on an entity with no id.
	 */
	@Test(expected = IllegalArgumentException.class)
	@Override
	public void TestNonIdempotentCreate() throws Exception
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().create(entity);
	}

	/**
	 * Tests what happens when a "dao#create"  is called on an entity with an id.
	 */
	@Test
	@Override
	public void TestIdempotentCreate() throws Exception
	{
		final T entity = getEmptyTestEntityWithKey();
		this.getDao().create(entity);
	}

	/////////////////////////////
	// ExistsInDatastore
	/////////////////////////////

	/**
	 * Tests what happens when a "dao#create"  is called on an entity with an id.
	 */
	@Test(expected = NullPointerException.class)
	public void TestExistsInDatastore_NullInput() throws Exception
	{
		this.getDao().existsInDatastore(null);
	}

	/**
	 * Tests what happens when a "dao#create"  is called on an entity with an id.
	 */
	@Test
	public void TestExistsInDatastore() throws Exception
	{
		final T entity = getEmptyTestEntityWithKey();
		this.getDao().create(entity);
		assertThat(this.getDao().existsInDatastore(entity.getTypedKey()), CoreMatchers.is(true));
	}

	/////////////////////////////
	// LoadFromDatastoreWithCursor
	/////////////////////////////

	@Test
	public void TestLoadFromDatastoreWithCursor() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestStringEntity> impl = new TestStringEntityDao();

		// Add 11 TestStringEntity entities to the Datastore.
		createTestStringEntitiesInDatastore(impl, 11);

		final Query<TestStringEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestStringEntity.class);
		ResultWithCursor<List<TestStringEntity>> actual = impl.loadFromDatastoreWithCursor(finalizedQuery, null, 10);

		assertThat(actual, CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), CoreMatchers.is(true));
		assertThat(actual.getResult(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getResult().size(), CoreMatchers.is(10));

		// Get the remainder...
		actual = impl.loadFromDatastoreWithCursor(finalizedQuery, actual.getOptCursor().get(), 9);
		assertThat(actual, CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), CoreMatchers.is(false));
		assertThat(actual.getResult(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getResult().size(), CoreMatchers.is(1));
	}

	/////////////////////////////
	// LoadFromDatastoreWithCursor
	/////////////////////////////

	@Test
	public void TestLoadKeysFromDatastoreWithCursor() throws Exception
	{
		// Need to peg this to type "TestStringEntity" in to test properly.
		final AbstractObjectifyDao<TestStringEntity> impl = new TestStringEntityDao();

		// Add 11 TestStringEntity entities to the Datastore.
		createTestStringEntitiesInDatastore(impl, 11);

		final Query<TestStringEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestStringEntity.class);
		ResultWithCursor<List<Key<TestStringEntity>>> actual = impl
				.loadKeysOnlyFromDatastoreWithCursor(finalizedQuery, null, 10);

		assertThat(actual, CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), CoreMatchers.is(true));
		assertThat(actual.getResult(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getResult().size(), CoreMatchers.is(10));

		// Get the remainder...
		actual = impl.loadKeysOnlyFromDatastoreWithCursor(finalizedQuery, actual.getOptCursor().get(), 9);
		assertThat(actual, CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), CoreMatchers.is(false));
		assertThat(actual.getResult(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getResult().size(), CoreMatchers.is(1));
	}

	/////////////////////////////
	// MassageQuery
	/////////////////////////////

	/**
	 * Test that this returns the first 10 out of 11 TestStringEntity objects from the Datastore, even though we're
	 * only
	 * asking for 9.
	 */
	@Test
	public void TestMassageQuery_String_NullCursor() throws Exception
	{
		// Need to peg this to type "TestStringEntity" in to test properly.
		final AbstractObjectifyDao<TestStringEntity> impl = new TestStringEntityDao();

		// Add 11 TestStringEntity entities to the Datastore.
		for (int i = 0; i < 11; i++)
		{
			final Key<TestStringEntity> key = Key.create(TestStringEntity.class, UUID.randomUUID().toString());
			final TestStringEntity testStringEntity = new TestStringEntity(key);
			impl.save(testStringEntity);
		}

		final Query<TestStringEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestStringEntity.class);
		final Query<TestStringEntity> newQuery = impl.massageQuery(finalizedQuery, null, 9);

		final List<TestStringEntity> results = newQuery.list();
		assertThat(results.size(), is(10));
	}

	@Test
	public void TestMassageQuery_0() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestStringEntity> impl = new TestStringEntityDao();
		final Query<TestStringEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestStringEntity.class)
				.filter("=", "foo");
		impl.massageQuery(finalizedQuery, Cursor.fromWebSafeString(""), 0);
	}
	/////////////////////////////
	// AssembleResultWithCursor
	/////////////////////////////

	/**
	 * Test that this returns the first 10 out of 11 TestStringEntity objects from the Datastore, even though we're
	 * only
	 * asking for 9.
	 */
	@Test
	public void TestAssembleResultWithCursor() throws Exception
	{
		// Need to peg this to type "TestStringEntity" in to test properly.
		final AbstractObjectifyDao<TestStringEntity> impl = new TestStringEntityDao();
		createTestStringEntitiesInDatastore(impl, 11);

		QueryResultIterator<TestStringEntity> iterator = ObjectifyService.ofy().load().type(TestStringEntity.class)
				.iterator();
		ResultWithCursor<List<TestStringEntity>> actual = impl.assembleResultWithCursor(iterator, 10);
		assertThat(actual, CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), CoreMatchers.is(true));
		assertThat(actual.getResult(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getResult().size(), CoreMatchers.is(10));

		// Get the remainder...
		iterator = ObjectifyService.ofy().load().type(TestStringEntity.class).startAt(actual.getOptCursor().get())
				.iterator();
		actual = impl.assembleResultWithCursor(iterator, 9);
		assertThat(actual, CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), CoreMatchers.is(false));
		assertThat(actual.getResult(), CoreMatchers.is(not(nullValue())));
		assertThat(actual.getResult().size(), CoreMatchers.is(1));

	}

	/////////////////////////////
	// ExistsInDatastoreConsistent
	/////////////////////////////

	@Test(expected = NullPointerException.class)
	public void TestExistsInDatastoreConsistent_NullInput() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();
		impl.existsInDatastoreConsistent(null);
	}

	@Test
	public void TestExistsInDatastoreConsistent() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestStringEntity> impl = new TestStringEntityDao();

		final Key<TestStringEntity> notFoundKey = Key.create(TestStringEntity.class, 2L + "");
		final Key<TestStringEntity> foundKey = Key.create(TestStringEntity.class, 1L + "");
		final TestStringEntity entity = new TestStringEntity(foundKey);
		impl.save(entity);

		assertThat(impl.existsInDatastoreConsistent(foundKey), is(true));
		assertThat(impl.existsInDatastoreConsistent(notFoundKey), is(false));
	}

	private void createTestStringEntitiesInDatastore(final AbstractObjectifyDao<TestStringEntity> impl,
			final int numToCreate)
	{
		// Add 11 TestStringEntity entities to the Datastore.
		for (int i = 0; i < numToCreate; i++)
		{
			final Key<TestStringEntity> key = Key.create(TestStringEntity.class, i + 1 + "");
			final TestStringEntity testStringEntity = new TestStringEntity(key);
			impl.save(testStringEntity);
		}
	}

	/////////////////////////////
	// Helper Methods
	/////////////////////////////

	/**
	 * Helper method to allow sub-classes to provide a valid entity for saving.
	 *
	 * @return
	 */
	protected T getEmptyTestEntityForValidSave() throws Exception
	{
		return this.getEmptyTestEntityWithKey();
	}

	/**
	 * Helper method to allow sub-classes to provide a valid entity for saving.
	 *
	 * @return
	 */
	protected T getEmptyTestEntityForValidCreate() throws Exception
	{
		return this.getEmptyTestEntityWithKey();
	}

	//
	//	/**
	//	 * Tests what happens when a #save method is called on a DAO with no id.
	//	 */
	//	@Test(expected = IllegalArgumentException.class)
	//	public void abstractObjectifyStringDaoTestor_TestNonIdempotentSave() throws Exception
	//	{
	//		final T entity = getEmptyTestEntityWithNoKey();
	//		this.getDao().save(entity);
	//	}

}
