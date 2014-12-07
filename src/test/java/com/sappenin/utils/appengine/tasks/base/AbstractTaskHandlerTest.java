package com.sappenin.utils.appengine.tasks.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.sappenin.utils.json.JsonUtils;
import com.sappenin.utils.json.JsonUtilsClassTypeMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * A unit test for {@link AbstractTaskScheduler}.
 */
public class AbstractTaskHandlerTest
{
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String DUMMY_PAYLOAD_JSON_STRING = "{\"dummyProperty\":\"DummyValue\"}";

	@Mock
	private JsonUtils jsonUtilsMock;

	@Mock
	private JsonUtilsClassTypeMapper jsonUtilsClassTypeMapperMock;

	@Mock
	private ObjectMapper objectMapperMock;

	@Mock
	private HttpServletRequest httpServletRequestMock;

	@Mock
	private HttpServletResponse httpServletResponseMock;

	private AbstractTaskHandler<DummyPayload> handler;

	private DummyPayload dummyPayload;

	@Before
	public void before() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		////////////////
		// Mock the HttpServletRequest to Return JSON
		final InputStream inputStream = new ByteArrayInputStream(
				DUMMY_PAYLOAD_JSON_STRING.getBytes(StandardCharsets.UTF_8));
		ServletInputStream servletInputStream = new ServletInputStream()
		{
			@Override
			public int read() throws IOException
			{
				return inputStream.read();
			}
		};
		when(this.httpServletRequestMock.getInputStream()).thenReturn(servletInputStream);
		////////////////

		this.handler = new AbstractTaskHandler<DummyPayload>(jsonUtilsMock, jsonUtilsClassTypeMapperMock)
		{
			// Sets the value of DummyProperty to "NewValue"
			@Override
			protected void handleHelper(final DummyPayload payload)
			{
				Preconditions.checkNotNull(payload);

				// Do something...
				payload.setDummyProperty("NewValue");
			}

			@Override
			protected Logger getLogger()
			{
				return logger;
			}
		};

		// DummyPayload to use and verify.
		this.dummyPayload = new DummyPayload();
	}

	///////////////////////////////
	// 	testHandle
	///////////////////////////////

	@Test(expected = NullPointerException.class)
	public void testHandle_NullHttpServletRequest() throws Exception
	{
		HttpServletRequest httpServletRequest = null;
		HttpServletResponse httpServletResponse = this.httpServletResponseMock;

		this.handler.handle(httpServletRequest, httpServletResponse);
	}

	@Test(expected = NullPointerException.class)
	public void testHandle_NullHttpServletResponse() throws Exception
	{
		HttpServletRequest httpServletRequest = this.httpServletRequestMock;
		HttpServletResponse httpServletResponse = null;

		this.handler.handle(httpServletRequest, httpServletResponse);
	}

	@Test(expected = NullPointerException.class)
	public void testHandle_NullPayloadFromJsonUtils() throws Exception
	{
		when(jsonUtilsMock.fromJson(httpServletRequestMock, jsonUtilsClassTypeMapperMock)).thenReturn(null);

		this.handler.handle(this.httpServletRequestMock, this.httpServletResponseMock);
	}

	@Test
	public void testHandle() throws Exception
	{
		final DummyPayload dummyPayload = new DummyPayload();
		when(jsonUtilsMock.fromJson(Mockito.<HttpServletRequest>any(), Mockito.<JsonUtilsClassTypeMapper>any()))
				.thenReturn(dummyPayload);

		this.handler.handle(this.httpServletRequestMock, this.httpServletResponseMock);

		assertThat(dummyPayload.getDummyProperty(), is("NewValue"));
	}

	///////////////////////////////
	// 	testHandleHelper(Payload)
	///////////////////////////////

	@Test(expected = NullPointerException.class)
	public void testHandleHelper_Null() throws Exception
	{
		this.handler.handleHelper(null);
	}

	@Test
	public void testHandleHelper() throws Exception
	{
		DummyPayload dummyPayload = new DummyPayload();
		this.handler.handleHelper(dummyPayload);

		assertThat(dummyPayload.getDummyProperty(), is("NewValue"));
	}

	///////////////////////////////
	// 	testHandleHelper(Payload, Request, Response)
	///////////////////////////////

	@Test(expected = NullPointerException.class)
	public void testHandleHelper_PayloadRequestResponse_NullPayload() throws Exception
	{
		this.handler.handleHelper(null, this.httpServletRequestMock, this.httpServletResponseMock);
	}

	@Test(expected = NullPointerException.class)
	public void testHandleHelper_PayloadRequestResponse_NullRequest() throws Exception
	{
		this.handler.handleHelper(dummyPayload, null, this.httpServletResponseMock);
	}

	@Test(expected = NullPointerException.class)
	public void testHandleHelper_PayloadRequestResponse_NullResponse() throws Exception
	{
		this.handler.handleHelper(dummyPayload, this.httpServletRequestMock, null);
	}

	@Test
	public void testHandleHelper_PayloadRequestResponse() throws Exception
	{
		DummyPayload dummyPayload = new DummyPayload();
		this.handler.handleHelper(dummyPayload, this.httpServletRequestMock, this.httpServletResponseMock);
		assertThat(dummyPayload.getDummyProperty(), is("NewValue"));
	}

	///////////////////////////////
	// Private Helpers
	///////////////////////////////

	@Getter
	@Setter
	private static final class DummyPayload
	{
		private String dummyProperty;
	}

}