package com.sappenin.utils.appengine.base;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.sappenin.utils.appengine.data.model.base.AbstractObjectifyLongEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
class TestLongEntity extends AbstractObjectifyLongEntity<TestLongEntity>
{
	private String testValue;

	public TestLongEntity(final Key<TestLongEntity> key)
	{
		this.setId(key == null ? null : key.getId());
	}
}
