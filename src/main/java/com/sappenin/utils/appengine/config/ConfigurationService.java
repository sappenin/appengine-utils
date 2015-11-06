/**
 * Copyright (C) 2014 UpSwell LLC (developers@theupswell.com)
 */
package com.sappenin.utils.appengine.config;

import io.instacount.api.services.config.InstacountConfigurationService.Config;

/**
 * An interface for accessing configuration data in a typed way.
 */
public interface ConfigurationService
{
	/**
	 * Return {@code true} if the app is running in AppEngine as the PRODUCTION application environment; or
	 * {@code false} otherwise. This method is structured in such a way as to make it ambiguous if the app is running in
	 * the development or testing environments. To overtly check for those conditions, use other methods in this
	 * service.
	 * 
	 * This setting is automated according to the following rules: </br>
	 * <ol>
	 * <li>If the app is running in AppEngine and the AppName is the production value (i.e., 'instacount-io'), return
	 * {@code true}</li>
	 * <li>If the app is running in AppEngine and the AppName is not the production value (e.g., 'instacount-io-test'),
	 * return {@code false}</li>
	 * <li>If the app is NOT running in AppEngine (e.g., in the development server), then return {@code false}.</li>
	 * </ol>
	 *
	 * sd * @return
	 */
	boolean isRunningOnProductionServer();

	/**
	 * Return {@code true} if the app is running in AppEngine in the TEST application environment; or {@code false}
	 * otherwise. This method is structured in such a way as to make it ambiguous if the app is running in the
	 * development or production environments. To overtly check for those conditions, use other methods in this service.
	 *
	 * This setting is automated according to the following rules: </br>
	 * <ol>
	 * <li>If the app is running in AppEngine and the AppName is the production value (i.e., 'instacount-io'), return
	 * {@code false}</li>
	 * <li>If the app is running in AppEngine and the AppName is not the production value (e.g., 'instacount-io-test'),
	 * return {@code true}</li>
	 * <li>If the app is NOT running in AppEngine (e.g., in the development server), then return {@code false}.</li>
	 * </ol>
	 *
	 * @return
	 */
	boolean isRunningOnTestServer();

	/**
	 * Return {@code true} if the app is running in AppEngine in the DEVELOPMENT application environment; or
	 * {@code false} otherwise. This method is structured in such a way as to make it ambiguous if the app is running in
	 * the production or testing environments. To overtly check for those conditions, use other methods in this service.
	 *
	 * This setting is automated according to the following rules: </br>
	 * <ol>
	 * <li>If the app is running in AppEngine and the AppName is the production value (i.e., 'instacount-io'), return
	 * {@code false}</li>
	 * <li>If the app is running in AppEngine and the AppName is not the production value (e.g., 'instacount-io-test'),
	 * return {@code false}</li>
	 * <li>If the app is NOT running in AppEngine (e.g., in the development server), then return {@code true}.</li>
	 * </ol>
	 *
	 * @return
	 */
	boolean isRunningOnDevServer();

	/**
	 * Return the current configuration for the application. This varies between dev, test, and prod.
	 *
	 * @return
	 */
	Config getCurrentConfig();

}
