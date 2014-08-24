package com.sappenin.utils.appengine.tasks.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sappenin.utils.json.JsonUtils;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A unit test for {@link AbstractTaskScheduler}.
 */
public class AbstractTaskSchedulerTest
{
	private static final String PROCESSING_QUEUE_NAME_TEST = "processingQueueNameTest";

	private static final String PROCESSING_QUEUE_URL_TEST = "processingQueueUrlTest";

	private static final String HELLO = "Hello";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Mock
	JsonUtils jsonUtils;

	private AbstractTaskScheduler<DummyPayload> impl;

	@Before
	public void before() throws JsonProcessingException
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(jsonUtils.toJSON(Mockito.<DummyPayload>any())).thenReturn("{dummyJson}");

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

	@Test(expected = NullPointerException.class)
	public void testGetJsonPayloadFromInputStreamRequest_Null() throws Exception
	{
		InputStream inputStream = null;
		impl.getJsonPayloadFromRequest(inputStream);
	}

	@Test
	public void testGetJsonPayloadFromInputStreamRequest() throws Exception
	{
		try (InputStream inputStream = new ByteArrayInputStream(HELLO.getBytes(StandardCharsets.UTF_8)))
		{
			String actual = impl.getJsonPayloadFromRequest(inputStream);
			assertThat(actual, is(HELLO));
		}
	}

	@Test
	public void testGetJsonPayloadFromHttpServletRequest() throws Exception
	{
		InputStream inputStream = new ByteArrayInputStream(HELLO.getBytes(StandardCharsets.UTF_8));
		ServletInputStream servletInputStreamMock = new DelegatingServletInputStream(inputStream);

		HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
		Mockito.when(httpServletRequest.getInputStream()).thenReturn(servletInputStreamMock);

		String actual = impl.getJsonPayloadFromRequest(httpServletRequest);

		assertThat(actual, is(HELLO));
	}

	@Test
	public void testGetJsonUtils() throws Exception
	{
		assertThat(impl.getJsonUtils(), is(jsonUtils));
	}

	@Data
	private static final class DummyPayload
	{
		private final String dummyProperty;
	}

	private static class DelegatingServletInputStream extends ServletInputStream
	{
		private final InputStream sourceStream;

		/**
		 * Create a DelegatingServletInputStream for the given source stream.
		 *
		 * @param sourceStream the source stream (never <code>null</code>)
		 */
		public DelegatingServletInputStream(InputStream sourceStream)
		{
			this.sourceStream = sourceStream;
		}

		/**
		 * Return the underlying source stream (never <code>null</code>).
		 */
		public final InputStream getSourceStream()
		{
			return this.sourceStream;
		}

		public int read() throws IOException
		{
			return this.sourceStream.read();
		}

		public void close() throws IOException
		{
			super.close();
			this.sourceStream.close();
		}

	}
}