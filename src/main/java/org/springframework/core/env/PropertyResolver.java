/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.core.env;

/**
 * 属性解决器
 */
public interface PropertyResolver {

	/**
	 * 是否包含该变量
	 */
	boolean containsProperty(String key);

	/**
	 * 返回与给定键关联的属性值，没有返回null
	 */
	String getProperty(String key);

	/**
	 * 返回与给定键关联的属性值，没有则返回默认值
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * 返回与给定键关联的属性值，可转换成指定类型
	 */
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * 返回与给定键关联的属性值，可转换成指定类型，如果没有，则返回默认值
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * 返回与给定键关联的属性值，可转换成指定类型
	 */
	@Deprecated
	<T> Class<T> getPropertyAsClass(String key, Class<T> targetType);

	/**
	 * 返回与给定键关联的属性值，没有则抛出IllegalStateException异常
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * 返回与给定键关联的属性值，可转换成指定类型，没有则抛出资格不合法异常IllegalStateException异常
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	/**
	 * 替换属性值中的占位符${...}，当无法替换时，保持原样返回
	 */
	String resolvePlaceholders(String text);

	/**
	 * 替换属性值中的占位符${...}，当无法替换时，抛出参数不合法异常
	 */
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}
