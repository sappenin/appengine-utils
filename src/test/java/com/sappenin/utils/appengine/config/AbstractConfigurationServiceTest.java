package com.sappenin.utils.appengine.config;

import com.google.appengine.api.utils.SystemProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link AbstractConfigurationService}.
 */
public class AbstractConfigurationServiceTest
{
	private static final String PRODUCTION_SERVER_APP_ID = "example-prod-app-id";

	private static final String TEST_SERVER_APP_ID = "example-test-app-id";

	private ConfigurationService configurationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		configurationService = new AbstractConfigurationService<Config>()
		{
			@Override
			public String getProductionEnvironmentAppId()
			{
				return PRODUCTION_SERVER_APP_ID;
			}

			@Override
			public Config getCurrentConfig()
			{
				return new Config("foo");
			}
		};
	}

	@Test
	public void testIsRunningOnDev() throws Exception
	{
		this.runningOnDevServer();
		assertThat(configurationService.isRunningOnDevServer(), is(true));
		this.runningOnTestServer();
		assertThat(configurationService.isRunningOnDevServer(), is(false));
		this.runningOnProductionServer();
		assertThat(configurationService.isRunningOnDevServer(), is(false));
	}

	@Test
	public void testIsRunningOnTest() throws Exception
	{
		this.runningOnDevServer();
		assertThat(configurationService.isRunningOnTestServer(), is(false));
		this.runningOnTestServer();
		assertThat(configurationService.isRunningOnTestServer(), is(true));
		this.runningOnProductionServer();
		assertThat(configurationService.isRunningOnTestServer(), is(false));
	}

	@Test
	public void testIsRunningOnProd() throws Exception
	{
		this.runningOnDevServer();
		assertThat(configurationService.isRunningOnProductionServer(), is(false));
		this.runningOnTestServer();
		assertThat(configurationService.isRunningOnProductionServer(), is(false));
		this.runningOnProductionServer();
		assertThat(configurationService.isRunningOnProductionServer(), is(true));
	}

	// ////////////////////////////
	// Helpers
	// ////////////////////////////

	/**
	 * A mocking method that initialized all sub-services into thinking they're running in the Development Server.
	 */
	private void runningOnDevServer()
	{
		SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
	}

	/**
	 * A mocking method that initialized all sub-services into thinking they're running in the Test Server on App
	 * Engine.
	 */
	private void runningOnTestServer()
	{
		SystemProperty.environment.set(SystemProperty.Environment.Value.Production);
		SystemProperty.applicationId.set(TEST_SERVER_APP_ID);
	}

	/**
	 * A mocking method that initialized all sub-services into thinking they're running in the Production Server on App
	 * Engine.
	 */
	private void runningOnProductionServer()
	{
		SystemProperty.environment.set(SystemProperty.Environment.Value.Production);
		SystemProperty.applicationId.set(PRODUCTION_SERVER_APP_ID);
	}

	@Getter
	@RequiredArgsConstructor
	@ToString
	@EqualsAndHashCode
	private static final class Config
	{
		private final String dummyConfigValue;
	}
}