/**
 * Copyright (C) 2015 Sappenin Inc (developers@sappenin.com)
 */
package com.sappenin.utils.appengine.config;

import com.google.appengine.api.utils.SystemProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract implementation of {@link ConfigurationService}.
 */
public abstract class AbstractConfigurationService<T> implements ConfigurationService<T>
{
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public boolean isRunningOnProductionServer()
	{
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
		{
			// The app is running in App Engine in the production application.
			if (SystemProperty.applicationId.get().equalsIgnoreCase(getProductionEnvironmentAppId()))
			{
				this.logger.log(Level.FINEST, String.format("Running on GAE Production Server (ApplicationId: %s)",
						SystemProperty.applicationId));
				return true;
			}
			else
			{
				// We're probably running in a test version on AppEngine (e.g., "instacount-io-test").
				this.logger.log(Level.FINEST,
						String.format("Running on GAE Test Server (ApplicationId: %s)", SystemProperty.applicationId));
				return false;
			}
		}
		else
		{
			// The app is NOT running on App Engine (likely on the DevServer)
			this.logger.log(Level.FINEST, String.format("Running in GAE Development Server (ApplicationId: %s)",
					SystemProperty.applicationId));
			return false;
		}
	}

	@Override
	public boolean isRunningOnTestServer()
	{
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
		{
			// The app is running in App Engine in the production application.
			if (SystemProperty.applicationId.get().equalsIgnoreCase(getProductionEnvironmentAppId()))
			{
				this.logger.log(Level.FINEST, String.format("Running on GAE Production Server (ApplicationId: %s)",
						SystemProperty.applicationId));
				return false;
			}
			else
			{
				// We're probably running in a test version on AppEngine (e.g., "instacount-io-test").
				this.logger.log(Level.FINEST,
						String.format("Running on GAE Test Server (ApplicationId: %s)", SystemProperty.applicationId));
				return true;
			}
		}
		else
		{
			// The app is NOT running on App Engine (likely on the DevServer)
			this.logger.log(Level.FINEST, String.format("Running in GAE Development Server (ApplicationId: %s)",
					SystemProperty.applicationId));
			return false;
		}
	}

	@Override
	public boolean isRunningOnDevServer()
	{
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
		{
			// The app is running in App Engine in the production application.
			if (SystemProperty.applicationId.get().equalsIgnoreCase(getProductionEnvironmentAppId()))
			{
				this.logger.log(Level.FINEST, String.format("Running on GAE Production Server (ApplicationId: %s)",
						SystemProperty.applicationId));
				return false;
			}
			else
			{
				// We're probably running in a test version on AppEngine (e.g., "instacount-io-test").
				this.logger.log(Level.FINEST,
						String.format("Running on GAE Test Server (ApplicationId: %s)", SystemProperty.applicationId));
				return false;
			}
		}
		else
		{
			// The app is NOT running on App Engine (likely on the DevServer)
			this.logger.log(Level.FINEST, String.format("Running in GAE Development Server (ApplicationId: %s)",
					SystemProperty.applicationId));
			return true;
		}
	}

	/**
	 * Allows implementations to provide the name of their production appengine identifier.  Corresponds to {@link
	 * SystemProperty#applicationId}.
	 *
	 * @return
	 */
	public abstract String getProductionEnvironmentAppId();

}
