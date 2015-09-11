package com.sappenin.utils.appengine.services;

import com.google.appengine.api.datastore.Blob;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

/**
 * A service for encrypting and decrypting data using the Java Simplified Encryption (i.e., "jasypt") library.
 *
 * @see "http://www.jasypt.org/"
 */
public interface AppengineEncryptionService
{
	/**
	 * Encrypt {@code plainText} using the provided {@code secretKey}.
	 *
	 * @param secretKey A private/secret key that is used to encrypt/decrypt data in a secure fashion.
	 * @param plainText A {@link String} representing some sensitive information that should be hidden.
	 *
	 * @return
	 */
	Blob encrypt(final String secretKey, final String plainText);

	/**
	 * Decrypt {@code encryptedText} using the provided secret key.
	 *
	 * @param secretKey     A {@link Blob} of data representing private/secret key that is used to encrypt/decrypt data
	 *                      in a secure fashion.
	 * @param encryptedText A {@link String} representing encrypted data that should be revealed.
	 *
	 * @return
	 */
	String decrypt(final String secretKey, final Blob encryptedText);

	/**
	 * A default implementation of {@link AppengineEncryptionService}.
	 */
	class Impl implements AppengineEncryptionService
	{
		@Override
		public Blob encrypt(final String secretKey, final String plainText)
		{
			final TextEncryptor textEncryptor = this.createTextEncryptor(secretKey);
			return new Blob(textEncryptor.encrypt(plainText).getBytes());
		}

		@Override
		public String decrypt(final String secretKey, final Blob encryptedText)
		{
			final TextEncryptor textEncryptor = this.createTextEncryptor(secretKey);
			return textEncryptor.decrypt(new String(encryptedText.getBytes()));
		}

		// ////////////////
		// Private Helpers
		// ////////////////

		/**
		 * Creates a new instance of {@link TextEncryptor} for use in this service.
		 *
		 * @param secretKey A private/secret key that is used to encrypt/decrypt data in a secure fashion.
		 *
		 * @para salt A random set of data that is used to
		 */
		private TextEncryptor createTextEncryptor(final String secretKey)
		{
			final StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
			// The Password must be US-ASCII, so we base64 encode and emit as a String.
			textEncryptor.setPassword(Base64.encodeBase64String(secretKey.getBytes()));
			return textEncryptor;
		}

	}

}
