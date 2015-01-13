/* 
 * Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.seiso.domain.meta;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import org.springframework.beans.BeanUtils;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.domain.entity.Item;

/**
 * {@link Item} wrapper to support dynamic access to a given item's properties and metamodel.
 * 
 * @author Willie Wheeler
 */
public class DynaItem {
	@Getter private final Class<?> itemClass;
	@Getter private final Item item;
	@Getter private PropertyDescriptor metaKeyProperty;
	@Getter private Serializable metaKey;

	public DynaItem(@NonNull Item item) {
		this.item = item;
		this.itemClass = item.getClass();

		String metaKeyFieldName = null;

		// Use currClass to search up the inheritance hierarchy. We need this for example to find the @Key for Vip
		// subclasses, since the @Key is defined in Vip.
		Class<?> currClass = itemClass;
		do {
			val fields = currClass.getDeclaredFields();
			for (val field : fields) {
				val anns = field.getAnnotations();
				for (val ann : anns) {
					if (ann.annotationType() == Key.class) {
						metaKeyFieldName = field.getName();
					}
				}
			}
		} while (metaKeyFieldName == null && (currClass = currClass.getSuperclass()) != null);
		
		// FIXME This doesn't handle compound keys. Use the ItemKey instead! [WLW]
		// If there's no explicit @Key, then use the ID by default.
		if (metaKeyFieldName == null) {
			metaKeyFieldName = "id";
		}

		this.metaKeyProperty = BeanUtils.getPropertyDescriptor(itemClass, metaKeyFieldName);
		val metaKeyGetter = metaKeyProperty.getReadMethod();

		try {
			this.metaKey = (Serializable) metaKeyGetter.invoke(item);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		// log.trace("Using key {}={} for itemClass={}", metaKeyFieldName, metaKey, itemClass.getSimpleName());
	}

	/**
	 * Returns the specified property value for the underlying item.
	 * 
	 * @param propertyName
	 *            Property name
	 * @return Property value
	 */
	@SneakyThrows
	public Object getPropertyValue(@NonNull String propertyName) {
		val desc = BeanUtils.getPropertyDescriptor(itemClass, propertyName);
		val getter = desc.getReadMethod();
		return getter.invoke(item);
	}
	
	/**
	 * Sets the specified property value on the underlying item.
	 * 
	 * @param propertyName
	 *            Property name
	 * @param propertyValue
	 *            Property value
	 */
	@SneakyThrows
	public void setPropertyValue(@NonNull String propertyName, Object propertyValue) {
		val desc = BeanUtils.getPropertyDescriptor(itemClass, propertyName);
		val setter = desc.getWriteMethod();
		setter.invoke(item, propertyValue);
	}
}
