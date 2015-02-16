package com.sappenin.utils.appengine.data.dao.base;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyStringEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Helper class to simulate an implementation of {@link AbstractObjectifyStringEntity}.
 */
@Entity
@NoArgsConstructor
@Data
public class TestStringEntity extends AbstractObjectifyStringEntity<TestStringEntity>
{
	private String testValue;

	public TestStringEntity(final Key<TestStringEntity> key)
	{
		this.setId(key == null ? null : key.getName());
	}
}
