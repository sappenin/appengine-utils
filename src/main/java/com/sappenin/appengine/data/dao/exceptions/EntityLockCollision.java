package com.sappenin.appengine.data.dao.exceptions;

/**
 * Thrown if an EntityLock is violated (Generally indicates that a particular field-value has already been locked). This
 * is mapped in {@link DefaultRestErrorResolver} as an Http CONFLICT.
 * 
 * @author David Fuelling <david@oodlemud.com>
 */
public class EntityLockCollision extends RuntimeException
{

	private static final long serialVersionUID = -7032656943142267322L;

	/**
	 * @param msg
	 */
	public EntityLockCollision(String msg)
	{
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public EntityLockCollision(String msg, Throwable cause)
	{
		super(msg, cause);
	}

}
