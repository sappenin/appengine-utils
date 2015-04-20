package com.sappenin.utils.appengine.tasks.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Optional;
import com.sappenin.utils.appengine.base.GaeTestHarnessInitializationAdapter;
import com.sappenin.utils.json.JsonUtils;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A unit test for {@link AbstractTaskScheduler}.
 */
public class AbstractTaskSchedulerTest extends GaeTestHarnessInitializationAdapter
{
	private static final String PROCESSING_QUEUE_NAME_TEST = "default";

	private static final String PROCESSING_QUEUE_URL_TEST = "/default";

	private static final String DUMMY_PROPERTY = "dummyProperty";

	private static final String TASK_123 = "task-123";

	//private static final String PROCESSING_QUEUE_HOST_TEST = "processingQueueHostTest";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Mock
	private JsonUtils jsonUtils;

	private AbstractTaskScheduler<DummyPayload> impl;

	@Before
	public void before() throws JsonProcessingException
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(jsonUtils.toJson(Mockito.<DummyPayload>any())).thenReturn("{dummyJson}");

		this.impl = new AbstractTaskScheduler<DummyPayload>(jsonUtils)
		{
			@Override
			protected Logger getLogger()
			{
				return logger;
			}

			@Override
			protected String getProcessingQueueName()
			{
				return PROCESSING_QUEUE_NAME_TEST;
			}

			@Override
			protected String getProcessingQueueUrlPath()
			{
				return PROCESSING_QUEUE_URL_TEST;
			}

			//			@Override
			//			protected String getHost()
			//			{
			//				return PROCESSING_QUEUE_HOST_TEST;
			//			}
		};
	}

	@Test
	public void testBuildTaskOptions() throws Exception
	{
		final DummyPayload dummyPayload = new DummyPayload("dummyPropertyTest");

		TaskOptions actual = impl.buildTaskOptions(dummyPayload, Optional.<String>absent());

		assertThat(actual.getEtaMillis(), is(nullValue()));
		assertThat(actual.getPayload(), is(notNullValue()));
		assertThat(actual.getUrl(), is(PROCESSING_QUEUE_URL_TEST));
	}

	@Test
	public void testGetLogger() throws Exception
	{
		assertThat(impl.getLogger(), is(logger));
	}

	@Test
	public void testGetProcessingQueueName() throws Exception
	{
		assertThat(impl.getProcessingQueueName(), is(PROCESSING_QUEUE_NAME_TEST));
	}

	@Test
	public void testGetProcessingQueueUrlPath() throws Exception
	{
		assertThat(impl.getProcessingQueueUrlPath(), is(PROCESSING_QUEUE_URL_TEST));
	}

	@Test
	public void testGetJsonUtils() throws Exception
	{
		assertThat(impl.getJsonUtils(), is(jsonUtils));
	}

	//////////////////
	// Test #schedule
	//////////////////

	@Test(expected = NullPointerException.class)
	public void testSchedule_NullPayload()
	{
		this.impl.schedule(null);
	}

	@Test
	public void testSchedule()
	{
		final TaskHandle handle = this.impl.schedule(new DummyPayload(DUMMY_PROPERTY));
		assertThat(handle.getName().startsWith("task"), is(true));
		assertThat(handle.getPayload(), is(not(nullValue())));
	}

	//////////////////
	// Test #schedule(payload, taskName)
	//////////////////

	@Test(expected = NullPointerException.class)
	public void testSchedulePayloadTaskName_NullPayload()
	{
		this.impl.schedule(null, TASK_123);
	}

	@Test
	public void testSchedulePayloadTaskName()
	{
		final TaskHandle handle = this.impl.schedule(new DummyPayload(DUMMY_PROPERTY), TASK_123);
		assertThat(handle.getName(), is(TASK_123));
		assertThat(handle.getPayload(), is(not(nullValue())));
	}

	//////////////////
	// Test #schedule(payload, optionalTaskName)
	//////////////////

	@Test(expected = NullPointerException.class)
	public void testScheduleOptional_NullPayload()
	{
		Optional<String> optTaskName = Optional.absent();
		this.impl.schedule(null, optTaskName);
	}

	@Test(expected = NullPointerException.class)
	public void testScheduleOptional_NullOptionalTaskName()
	{
		Optional<String> optTaskName = null;
		this.impl.schedule(new DummyPayload(DUMMY_PROPERTY), optTaskName);
	}

	@Test
	public void testScheduleOptional()
	{
		Optional<String> optTaskName = Optional.of(TASK_123);
		final TaskHandle handle = this.impl.schedule(new DummyPayload(DUMMY_PROPERTY), optTaskName);

		assertThat(handle.getName(), is(TASK_123));
		assertThat(handle.getPayload(), is(not(nullValue())));
	}

	//////////////////
	// Test #schedule
	//////////////////

	@Test(expected = NullPointerException.class)
	public void testScheduleAsync_NullPayload()
	{
		this.impl.scheduleAsync(null);
	}

	@Test
	public void testScheduleAsync() throws ExecutionException, InterruptedException
	{
		final Future<TaskHandle> futureHandle = this.impl.scheduleAsync(new DummyPayload(DUMMY_PROPERTY));
		final TaskHandle handle = futureHandle.get();
		assertThat(handle.getName().startsWith("task"), is(true));
		assertThat(handle.getPayload(), is(not(nullValue())));
	}

	//////////////////
	// Test #schedule(payload, taskName)
	//////////////////

	@Test(expected = NullPointerException.class)
	public void testScheduleAsyncPayloadTaskName_NullPayload()
	{
		this.impl.scheduleAsync(null, TASK_123);
	}

	@Test
	public void testScheduleAsyncPayloadTaskName() throws ExecutionException, InterruptedException
	{
		final Future<TaskHandle> futureHandle = this.impl.scheduleAsync(new DummyPayload(DUMMY_PROPERTY), TASK_123);
		final TaskHandle handle = futureHandle.get();
		assertThat(handle.getName(), is(TASK_123));
		assertThat(handle.getPayload(), is(not(nullValue())));
	}

	//////////////////
	// Test #schedule(payload, optionalTaskName)
	//////////////////

	@Test(expected = NullPointerException.class)
	public void testScheduleAsyncOptional_NullPayload()
	{
		Optional<String> optTaskName = Optional.absent();
		this.impl.scheduleAsync(null, optTaskName);
	}

	@Test(expected = NullPointerException.class)
	public void testScheduleAsyncOptional_NullOptionalTaskName()
	{
		Optional<String> optTaskName = null;
		this.impl.scheduleAsync(new DummyPayload(DUMMY_PROPERTY), optTaskName);
	}

	@Test
	public void testScheduleAsyncOptional() throws ExecutionException, InterruptedException
	{
		Optional<String> optTaskName = Optional.of(TASK_123);
		final Future<TaskHandle> futureHandle = this.impl.scheduleAsync(new DummyPayload(DUMMY_PROPERTY),
				optTaskName);

		final TaskHandle handle = futureHandle.get();
		assertThat(handle.getName(), is(TASK_123));
		assertThat(handle.getPayload(), is(not(nullValue())));
	}

	////////////////////
	// Private Helpers
	////////////////////

	@Data
	private static final class DummyPayload
	{
		private final String dummyProperty;
	}
}