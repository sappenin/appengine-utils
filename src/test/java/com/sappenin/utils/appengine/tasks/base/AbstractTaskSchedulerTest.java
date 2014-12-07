package com.sappenin.utils.appengine.tasks.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sappenin.utils.appengine.base.GaeTestHarnessInitializationAdapter;
import com.sappenin.utils.json.JsonUtils;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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

		TaskOptions actual = impl.buildTaskOptions(dummyPayload);

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

	@Test
	public void testSchedule()
	{
		this.impl.schedule(new DummyPayload("dummyProperty"));
	}

	@Data
	private static final class DummyPayload
	{
		private final String dummyProperty;
	}
}