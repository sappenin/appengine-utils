package com.sappenin.utils.appengine.base;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueueCallback;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalCapabilitiesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalImagesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalRdbmsServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalXMPPServiceTestConfig;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.impl.translate.opt.joda.DateTimeZoneTranslatorFactory;
import com.googlecode.objectify.impl.translate.opt.joda.ReadableInstantTranslatorFactory;
import com.googlecode.objectify.util.Closeable;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;

/**
 * An adapter class that JUnit tests can utilize in order to access Google Appengine Services in a TestHarnes.
 */
public class GaeTestHarnessInitializationAdapter
{
	private LocalServiceTestHelper helper = null;

	// This is 100 by default, meaning eventual consistency is simulated to fail
	// 100% of the time. Tests should set this to 0 before calling
	// GaeTestHarnessInitializationAdapter.setup in order to turn this off.
	// NOTE: There is a bug with 1.6.x and below Appengine such that if this is
	// set to 0, it disables HRD. So, we set it to 1 and hope that statistically
	// this doesn't fail the tests (at least not very often).
	public static float amountOfHRDFailureForEventualConsistency = 0.1f;

	// Unlike CountDownLatch, TaskCountDownlatch lets us reset.
	protected final LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig
			.TaskCountDownLatch(
			1);

	// New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
	protected Closeable session;

	/**
	 * Initialize the Appengine test harness for a particular JUnit test.
	 */
	@Before
	public void setUpAppengine() throws Exception
	{
		this.setUpAppengineInternal(null, null);

		helper.setUp();

		// New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
		ObjectifyService.factory().getTranslators().add(new DateTimeZoneTranslatorFactory());
		ObjectifyService.factory().getTranslators().add(new ReadableInstantTranslatorFactory());
		this.session = ObjectifyService.begin();
	}

	/**
	 * Cleanup Objectify
	 */
	@After
	public void cleanupAppengine()
	{
		AsyncCacheFilter.complete();
		latch.reset();

		// New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
		this.session.close();

		helper.tearDown();
	}

	/**
	 * Initialize the Appengine test harness for a particular JUnit test.
	 */
	protected void setUpAppengineInternal(final String queueXMLPath,
			final Class<? extends LocalTaskQueueCallback> localTaskQueueCallbackClass) throws Exception
	{
		final LocalTaskQueueTestConfig localTaskQueueConfig = initLocalTaskQueues(queueXMLPath,
				localTaskQueueCallbackClass);

		// See
		// http://code.google.com/appengine/docs/java/tools/localunittesting.html#Writing_HRD_Datastore_Tests
		final LocalDatastoreServiceTestConfig hrdConfig = new LocalDatastoreServiceTestConfig()
				.setDefaultHighRepJobPolicyUnappliedJobPercentage(
						GaeTestHarnessInitializationAdapter.amountOfHRDFailureForEventualConsistency)
				.setNoStorage(true);

		// ///////////////////////////
		// Create the Helper
		// ///////////////////////////

		helper = new LocalServiceTestHelper(new LocalBlobstoreServiceTestConfig(),
				new LocalCapabilitiesServiceTestConfig(), new LocalImagesServiceTestConfig(),
				new LocalMailServiceTestConfig(), new LocalMemcacheServiceTestConfig(),
				new LocalRdbmsServiceTestConfig(), localTaskQueueConfig, new LocalURLFetchServiceTestConfig(),
				new LocalUserServiceTestConfig(), new LocalXMPPServiceTestConfig(), hrdConfig);
	}

	/**
	 * Initializes and returns the Local TaskQueue Test Queues.
	 *
	 * @param queueXMLPath
	 * @param localTaskQueueCallbackClass
	 *
	 * @return
	 */
	protected final LocalTaskQueueTestConfig initLocalTaskQueues(String queueXMLPath,
			final Class<? extends LocalTaskQueueCallback> localTaskQueueCallbackClass)
	{

		if (StringUtils.isBlank(queueXMLPath))
		{
			queueXMLPath = "war/WEB-INF/queue.xml";
		}

		// Create the LocalTaskQueueConfig with the latch and callback
		// information. See
		// http://code.google.com/appengine/docs/java/tools/localunittesting.html#Writing_Task_Queue_Tests
		final LocalTaskQueueTestConfig localTaskQueueConfig = new LocalTaskQueueTestConfig()
				.setDisableAutoTaskExecution(false).setQueueXmlPath(queueXMLPath).setTaskExecutionLatch(latch);

		if (localTaskQueueCallbackClass != null)
		{
			localTaskQueueConfig.setCallbackClass(localTaskQueueCallbackClass);
		}
		else
		{
			localTaskQueueConfig.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class);
		}

		return localTaskQueueConfig;
	}

	/**
	 * Sets the amount of HRD failure to simulate. For example, setting this to 100 will simulate an HRD failure
	 * 100% of
	 * the time.
	 *
	 * @param amountOfFailure
	 */
	protected void setAmountOfHRDFailurePercentage(final float amountOfFailure)
	{
		System.out.println("Setting amountOfHRDFailureForEventualConsistency to " + amountOfFailure);
	}
}
