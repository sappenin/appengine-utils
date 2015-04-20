package com.sappenin.utils.appengine.data.dao.base;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.impl.translate.opt.joda.DateTimeZoneTranslatorFactory;
import com.googlecode.objectify.impl.translate.opt.joda.ReadableInstantTranslatorFactory;
import com.sappenin.utils.appengine.data.dao.ObjectifyLongDao;
import com.sappenin.utils.appengine.data.dao.base.TestLongEntityTest.TestLongEntityDao;
import com.sappenin.utils.appengine.data.model.GaeTypedEntity;
import com.sappenin.utils.appengine.data.model.ResultWithCursor;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyLongEntity;
import org.hamcrest.core.Is;
import org.junit.Before;
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
public abstract class AbstractObjectifyLongObjectifyDaoTester<T extends AbstractObjectifyLongEntity<T> &
		GaeTypedEntity<T>>
		extends AbstractObjectifyDaoTester<T>
{
	@Before
	public void beforeAbstractObjectifyLongDaoTester()
	{
		ObjectifyService.factory().getTranslators().add(new DateTimeZoneTranslatorFactory());
		ObjectifyService.factory().getTranslators().add(new ReadableInstantTranslatorFactory());
		ObjectifyService.factory().register(TestLongEntity.class);
	}

	/**
	 * Returns the Dao for this AbstractObjectifyLongDao Testor.
	 *
	 * @return
	 */
	@Override
	protected abstract ObjectifyLongDao<T> getDao();

	@Override
	public T getExistingEntityFromDatastore()
	{
		final T entity = this.getFullyPopulatedEntity();
		this.getDao().create(entity);
		return entity;
	}

	/**
	 * Tests what happens when a "dao#save" is called on an entity without an id.
	 */
	@Test(expected = NullPointerException.class)
	@Override
	public void TestSaveWithoutId()
	{
		final T entity = getExistingEntityFromDatastore();
		entity.setId(null);
		this.getDao().save(entity);
	}

	/**
	 * Tests what happens when the "dao#create"  is called on an entity with no id.
	 */
	@Test
	@Override
	public void TestNonIdempotentCreate()
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().create(entity);
	}

	/**
	 * Tests what happens when a "dao#create"  is called on an entity with an id.
	 */
	@Test(expected = IllegalArgumentException.class)
	@Override
	public void TestIdempotentCreate()
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().create(entity);
		this.getDao().create(entity);
	}

	@Override
	@Test
	public void TestFullyPopulatedEntity()
	{
		final T entity = this.getFullyPopulatedEntity();
		this.getDao().create(entity);

		final Optional<T> optLoadedEntity = this.getDao().findByTypedKey(entity.getTypedKey());
		assertThat(optLoadedEntity.isPresent(), is(true));
		final T loadedEntity = optLoadedEntity.get();
		this.doFullEntityAssertions(loadedEntity);
	}

	/////////////////////////////
	// ExistsInDatastore
	/////////////////////////////

	/**
	 * Tests what happens when a "dao#existsInDatastore"  is called with a null input.
	 */
	@Test(expected = NullPointerException.class)
	public void TestExistsInDatastore_NullInput()
	{
		this.getDao().existsInDatastore(null);
	}

	@Test
	public void TestExistsInDatastore_NoExists()
	{
		final T entity = getEmptyTestEntityWithNoKey();
		entity.setId(123L);
		final Key<T> key = entity.getTypedKey();
		ObjectifyService.ofy().delete().key(key).now();
		assertThat(this.getDao().existsInDatastore(key), is(false));
	}

	/**
	 * Tests what happens when a "dao#existsInDatastore" is called and the entity should exist.
	 */
	@Test
	public void TestExistsInDatastore_Exists()
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().create(entity);
		assertThat(this.getDao().existsInDatastore(entity.getTypedKey()), is(true));
	}

	/////////////////////////////
	// LoadFromDatastoreWithCursor
	/////////////////////////////

	@Test
	public void TestLoadFromDatastoreWithCursor()
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
	public void TestLoadKeysFromDatastoreWithCursor()
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
	public void TestMassageQuery_Long_NullCursor()
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
	public void TestMassageQuery_0()
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
	public void TestAssembleResultWithCursor()
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
	public void TestExistsInDatastoreConsistent_NullInput()
	{
		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();
		impl.existsInDatastoreConsistent(null);
	}

	@Test
	public void TestExistsInDatastoreConsistent()
	{
		final T entity = this.getEmptyTestEntityWithNoKey();
		this.getDao().create(entity);

		// Need to peg this to type "TestLongEntity" in to test properly.
		final AbstractObjectifyDao<TestLongEntity> impl = new TestLongEntityDao();

		final Key<TestLongEntity> notFoundKey = Key.create(TestLongEntity.class, 99999999999L);
		final Key<TestLongEntity> foundKey = Key.create(TestLongEntity.class, entity.getId());

		assertThat(impl.existsInDatastoreConsistent(foundKey), is(true));
		assertThat(impl.existsInDatastoreConsistent(notFoundKey), is(false));
	}

	/////////////////////////////
	// Helper Methods
	/////////////////////////////

	private void createLongEntitiesInDatastore(final AbstractObjectifyDao<TestLongEntity> impl, final int numToCreate)
	{
		for (int i = 0; i < numToCreate; i++)
		{
			//final Key<TestLongEntity> key = Key.create(TestLongEntity.class, i + 1);
			final TestLongEntity testLongEntity = new TestLongEntity();
			impl.create(testLongEntity);
		}
	}
}
