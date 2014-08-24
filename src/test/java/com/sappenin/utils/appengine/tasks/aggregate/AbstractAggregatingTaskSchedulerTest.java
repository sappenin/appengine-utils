package com.sappenin.utils.appengine.tasks.aggregate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sappenin.utils.appengine.tasks.base.AbstractTaskScheduler;
import com.sappenin.utils.json.JsonUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class AbstractAggregatingTaskSchedulerTest
{
	private static final String PROCESSING_QUEUE_NAME_TEST = "processingQueueNameTest";

	private static final String PROCESSING_QUEUE_URL_TEST = "processingQueueUrlTest";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Mock
	JsonUtils jsonUtils;

	private AbstractAggregatingTaskScheduler<DummyPayloadWithName> impl;

	@Before
	public void before() throws JsonProcessingException
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(jsonUtils.toJSON(Mockito.<DummyPayload>any())).thenReturn("{dummyJson}");

		this.impl = new AbstractAggregatingTaskScheduler<DummyPayloadWithName>(jsonUtils)
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
		};
	}

	@Test
	public void testBuildTaskOptions() throws Exception
	{
		final DummyPayloadWithName dummyPayloadWithName = new DummyPayloadWithName("dummyPropertyTest");

		TaskOptions actual = impl.buildTaskOptions(dummyPayloadWithName);

		assertThat(actual.getEtaMillis(), is(notNullValue()));
		assertThat(actual.getPayload(), is(notNullValue()));
		assertThat(actual.getUrl(), is(PROCESSING_QUEUE_URL_TEST));
	}

	@Test
	public void testGetJsonUtils() throws Exception
	{
		assertThat(impl.getJsonUtils(), is(jsonUtils));
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	@EqualsAndHashCode
	@ToString
	private static class DummyPayload
	{
		@NonNull
		private final String dummyProperty;
	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	private static class DummyPayloadWithName extends DummyPayload implements AggregatingTaskSchedulerPayload
	{
		public DummyPayloadWithName(String dummyProperty)
		{
			super(dummyProperty);
		}

		@Override
		public String getAggregatedTaskName()
		{
			return "aggTaskName";
		}
	}

}