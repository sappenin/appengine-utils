package com.sappenin.utils.appengine.data.locks;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Entity;
import com.sappenin.utils.appengine.base.GaeTestHarnessInitializationAdapter;
import com.sappenin.utils.appengine.data.dao.exceptions.EntityLockCollision;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * A unit test for {@link ObjectifyEntityLockHelper}.
 */
public class ObjectifyEntityLockHelperTest extends GaeTestHarnessInitializationAdapter
{
	private ObjectifyEntityLockHelper helper;

	@Before
	public void beforeEntityLockHelperTest()
	{
		helper = new ObjectifyEntityLockHelper();

		ObjectifyService.ofy().factory().register(UsernameLock.class);
	}

	///////////////////////////
	// reserveLockValue
	///////////////////////////

	@Test(expected = NullPointerException.class)
	public void testReserveLockValue_NoTransaction() throws Exception
	{
		final UsernameLock usernameLock = new UsernameLock("testUserName");
		helper.reserveLockValue(usernameLock);
	}

	@Test(expected = NullPointerException.class)
	public void testReserveLockValue_NullInput() throws Exception
	{
		final UsernameLock usernameLock = new UsernameLock("testUserName");
		helper.reserveLockValue(usernameLock);
	}

	@Test
	public void testReserveLockValue() throws Exception
	{
		lockUsernameHelper();
	}

	@Test(expected = EntityLockCollision.class)
	public void testReserveLockValue_ValueLocked() throws Exception
	{
		this.lockUsernameHelper();
		try
		{
			// Try to lock the same thing again, and expect an ELC.
			lockUsernameHelper();
		}
		catch (EntityLockCollision elc)
		{
			throw elc;
		}
		catch (Exception e)
		{
			fail();
		}
	}

	///////////////////////////
	// removeLocksSafely
	///////////////////////////

	@Test(expected = NullPointerException.class)
	public void testRemoveLocksSafely_NoTransaction() throws Exception
	{
		final UsernameLock lock = this.lockUsernameHelper();
		this.helper.removeLocksSafely(lock);
	}

	@Test(expected = NullPointerException.class)
	public void testRemoveLocksSafely_NullInput() throws Exception
	{
		this.helper.removeLocksSafely(null);
	}

	@Test
	public void testRemoveLocksSafely() throws Exception
	{
		final UsernameLock lock = this.lockUsernameHelper();
		ObjectifyService.ofy().transact(new VoidWork()
		{
			@Override
			public void vrun()
			{
				helper.removeLocksSafely(lock);
			}
		});
	}

	/**
	 * Helper class.
	 */
	@Entity
	private static final class UsernameLock extends AbstractEntityStringLock
	{
		/**
		 * @deprecated Exists only for Objectify
		 */
		@Deprecated
		public UsernameLock()
		{
		}

		public UsernameLock(final String entityFieldValue)
		{
			super(entityFieldValue);
		}
	}

	/**
	 *
	 */
	private UsernameLock lockUsernameHelper()
	{
		UsernameLock usernameLock = null;
		try
		{
			usernameLock = ObjectifyService.ofy().transact(new Work<UsernameLock>()
			{
				@Override
				public UsernameLock run()
				{
					final UsernameLock usernameLock = new UsernameLock("testUserName");
					helper.reserveLockValue(usernameLock);
					return usernameLock;
				}
			});
		}
		catch (EntityLockCollision elc)
		{
			// No exception expected here.
			throw elc;
		}
		catch (Exception e)
		{
			// No exception expected here.
			fail();
		}
		return usernameLock;
	}

}