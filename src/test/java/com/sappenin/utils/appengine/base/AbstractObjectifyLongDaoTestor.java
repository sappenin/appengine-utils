package com.sappenin.utils.appengine.base;

import com.sappenin.utils.appengine.data.dao.ObjectifyLongDao;
import com.sappenin.utils.appengine.data.model.GaeTypedEntity;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyLongEntity;
import org.junit.Test;

/**
 * Provides common functionality for base DAO's, such as create, update, and delete functions.
 *
 * @param <{@link T} extends {@link BaseModelLong}>
 *
 * @author David Fuelling
 */
public abstract class AbstractObjectifyLongDaoTestor<T extends AbstractObjectifyLongEntity<T> & GaeTypedEntity<T>>
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
	@Test
	@Override
	public void TestNonIdempotentSave() throws Exception
	{
		final T entity = getEmptyTestEntityWithNoKey();
		this.getDao().save(entity);
	}

	/**
	 * Tests what happens when a "dao#save"  is called on an entity with an id.
	 */
	@Test(expected = IllegalArgumentException.class)
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
	@Test
	@Override
	public void TestIdempotentCreate() throws Exception
	{
		final T entity = getEmptyTestEntityWithKey();
		this.getDao().create(entity);
	}

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
}
