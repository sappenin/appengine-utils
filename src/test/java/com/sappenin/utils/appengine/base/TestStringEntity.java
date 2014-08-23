package com.sappenin.utils.appengine.base;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyStringEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
class TestStringEntity extends AbstractObjectifyStringEntity<TestStringEntity>
{
	private String testValue;

	public TestStringEntity(final Key<TestStringEntity> key)
	{
		this.setId(key == null ? null : key.getName());
	}
}
