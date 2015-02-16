package com.sappenin.utils.appengine.data.dao.base;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.sappenin.utils.appengine.data.dao.ObjectifyLongDao;
import com.sappenin.utils.appengine.data.dao.base.TestLongEntityTest.TestLongEntityDao;
import com.sappenin.utils.appengine.data.model.GaeTypedEntity;
import com.sappenin.utils.appengine.data.model.ResultWithCursor;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyLongEntity;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Provides common functionality for base DAO's, such as create, update, and delete functions.
 *
 * @param <{@link T} extends {@link BaseModelLong}>
 *
 * @author David Fuelling
 */
public abstract class AbstractObjectifyLongDaoTester<T extends AbstractObjectifyLongEntity<T> & GaeTypedEntity<T>>
		extends AbstractDaoTesterGAE<T>
{
	/**
	 * Returns the Dao for this AbstractObjectifyLongDao Testor.
	 *
	 * @return
	 */
	@Override
	protected abstract ObjectifyLongDao<T> getDao();

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
	@Test
	@Override
	public void TestNonIdempotentCreate() throws Exception
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().create(entity);
	}

	/**
	 * Tests what happens when a "dao#create"  is called on an entity with an id.
	 */
	@Test(expected = IllegalArgumentException.class)
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
	@Test(expected = IllegalArgumentException.class)
	public void TestExistsInDatastore() throws Exception
	{
		final T entity = getEmptyTestEntityWithKey();
		this.getDao().create(entity);
		assertThat(this.getDao().existsInDatastore(entity.getTypedKey()), is(true));
	}

	/////////////////////////////
	// LoadFromDatastoreWithCursor
	/////////////////////////////

	@Test
	public void TestLoadFromDatastoreWithCursor() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		// Add 11 TestLongEntity entities to the Datastore.
		createLongEntitiesInDatastore(impl, 11);

		final Query<TestLongEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestLongEntity.class);
		ResultWithCursor<List<TestLongEntity>> actual = impl.loadFromDatastoreWithCursor(finalizedQuery, null, 10);

		assertThat(actual, is(not(nullValue())));
		assertThat(actual.getOptCursor(), is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), is(true));
		assertThat(actual.getResult(), is(not(nullValue())));
		assertThat(actual.getResult().size(), is(10));

		// Get the remainder...
		actual = impl.loadFromDatastoreWithCursor(finalizedQuery, actual.getOptCursor().get(), 9);
		assertThat(actual, is(not(nullValue())));
		assertThat(actual.getOptCursor(), is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), is(false));
		assertThat(actual.getResult(), is(not(nullValue())));
		assertThat(actual.getResult().size(), is(1));
	}

	/////////////////////////////
	// LoadFromDatastoreWithCursor
	/////////////////////////////

	@Test
	public void TestLoadKeysFromDatastoreWithCursor() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		// Add 11 TestLongEntity entities to the Datastore.
		createLongEntitiesInDatastore(impl, 11);

		final Query<TestLongEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestLongEntity.class);
		ResultWithCursor<List<Key<TestLongEntity>>> actual = impl
				.loadKeysOnlyFromDatastoreWithCursor(finalizedQuery, null, 10);

		assertThat(actual, is(not(nullValue())));
		assertThat(actual.getOptCursor(), is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), is(true));
		assertThat(actual.getResult(), is(not(nullValue())));
		assertThat(actual.getResult().size(), is(10));

		// Get the remainder...
		actual = impl.loadKeysOnlyFromDatastoreWithCursor(finalizedQuery, actual.getOptCursor().get(), 9);
		assertThat(actual, is(not(nullValue())));
		assertThat(actual.getOptCursor(), is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), is(false));
		assertThat(actual.getResult(), is(not(nullValue())));
		assertThat(actual.getResult().size(), is(1));
	}

	/////////////////////////////
	// MassageQuery
	/////////////////////////////

	/**
	 * Test that this returns the first 10 out of 11 TestLongEntity objects from the Datastore, even though we're only
	 * asking for 9.
	 */
	@Test
	public void TestMassageQuery_Long_NullCursor() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		// Add 11 TestLongEntity entities to the Datastore.
		createLongEntitiesInDatastore(impl, 11);

		final Query<TestLongEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestLongEntity.class);
		final Query<TestLongEntity> newQuery = impl.massageQuery(finalizedQuery, null, 9);

		final List<TestLongEntity> results = newQuery.list();
		assertThat(results.size(), Is.is(10));
	}

	@Test
	public void TestMassageQuery_0() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();
		final Query<TestLongEntity> finalizedQuery = ObjectifyService.ofy().load().type(TestLongEntity.class)
				.filter("=", "foo");
		impl.massageQuery(finalizedQuery, Cursor.fromWebSafeString(""), 0);
	}

	/////////////////////////////
	// AssembleResultWithCursor
	/////////////////////////////

	/**
	 * Test that this returns the first 10 out of 11 TestLongEntity objects from the Datastore, even though we're only
	 * asking for 9.
	 */
	@Test
	public void TestAssembleResultWithCursor() throws Exception
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		// Add 11 TestLongEntity entities to the Datastore.
		createLongEntitiesInDatastore(impl, 11);

		QueryResultIterator<TestLongEntity> iterator = ObjectifyService.ofy().load().type(TestLongEntity.class)
				.iterator();
		ResultWithCursor<List<TestLongEntity>> actual = impl.assembleResultWithCursor(iterator, 10);
		assertThat(actual, is(not(nullValue())));
		assertThat(actual.getOptCursor(), is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), is(true));
		assertThat(actual.getResult(), is(not(nullValue())));
		assertThat(actual.getResult().size(), is(10));

		// Get the remainder...
		iterator = ObjectifyService.ofy().load().type(TestLongEntity.class).startAt(actual.getOptCursor().get())
				.iterator();
		actual = impl.assembleResultWithCursor(iterator, 9);
		assertThat(actual, is(not(nullValue())));
		assertThat(actual.getOptCursor(), is(not(nullValue())));
		assertThat(actual.getOptCursor().isPresent(), is(false));
		assertThat(actual.getResult(), is(not(nullValue())));
		assertThat(actual.getResult().size(), is(1));

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
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		final Key<TestLongEntity> notFoundKey = Key.create(TestLongEntity.class, 2L);
		final Key<TestLongEntity> foundKey = Key.create(TestLongEntity.class, 1L);
		final TestLongEntity entity = new TestLongEntity(foundKey);
		impl.save(entity);

		assertThat(impl.existsInDatastoreConsistent(foundKey), is(true));
		assertThat(impl.existsInDatastoreConsistent(notFoundKey), is(false));
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
		return this.getEmptyTestEntityWithNoKey();
	}

	private void createLongEntitiesInDatastore(final AbstractObjectifyDao<TestLongEntity> impl, final int numToCreate)
	{
		for (int i = 0; i < numToCreate; i++)
		{
			final Key<TestLongEntity> key = Key.create(TestLongEntity.class, i + 1);
			final TestLongEntity testLongEntity = new TestLongEntity(key);
			impl.save(testLongEntity);
		}
	}
}
