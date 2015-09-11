package com.sappenin.utils.appengine.services;

import com.google.appengine.api.datastore.Blob;
import com.sappenin.utils.appengine.base.GaeTestHarnessInitializationAdapter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for {@link AppengineEncryptionService}.
 */
public class AppengineEncryptionServiceTest extends GaeTestHarnessInitializationAdapter
{
	private AppengineEncryptionService appengineEncryptionService;

	private static final int NUM_LOOPS = 50;

	@Before
	public void setUp() throws Exception
	{
		this.appengineEncryptionService = new AppengineEncryptionService.Impl();
	}

	@Test
	public void testEncryptDecryptWithSameEncryptionSecretKey() throws Exception
	{
		final String secretKey = RandomStringUtils.random(10);
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			final String plainText = RandomStringUtils.random(20) + "-" + i;
			this.doEncryptionAssertions(secretKey, plainText);
		}
	}

	@Test
	public void testEncryptDecryptWithDifferentEncryptionSecrets() throws Exception
	{
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			final String secretKey = RandomStringUtils.random(20) + "-" + i;
			final String plainText = RandomStringUtils.random(20) + "-" + i;
			this.doEncryptionAssertions(secretKey, plainText);
		}
	}

	@Test
	public void testEncryptDecryptWithLongEncryptionSecrets() throws Exception
	{
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			final String secretKey = RandomStringUtils.random(100) + "-" + i;
			final String plainText = RandomStringUtils.random(200) + "-" + i;
			this.doEncryptionAssertions(secretKey, plainText);
		}
	}

	@Test
	public void testEncryptDecryptWithLongEncryptionSecretsInverted() throws Exception
	{
		for (int i = 0; i < NUM_LOOPS; i++)
		{
			final String secretKey = RandomStringUtils.random(200) + "-" + i;
			final String plainText = RandomStringUtils.random(100) + "-" + i;
			this.doEncryptionAssertions(secretKey, plainText);
		}
	}

	//////////////////
	// Private Helpers
	//////////////////

	private void doEncryptionAssertions(final String secretKey, final String plainText)
	{
		Blob encryptedValue = this.appengineEncryptionService.encrypt(secretKey, plainText);
		assertThat(this.appengineEncryptionService.decrypt(secretKey, encryptedValue), is(plainText));

		encryptedValue = this.appengineEncryptionService.encrypt(secretKey, plainText);
		assertThat(this.appengineEncryptionService.decrypt(secretKey, encryptedValue), is(plainText));
	}

}