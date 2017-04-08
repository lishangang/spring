/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.core;

/**
 * 属性存储器
 *
 * @author Rob Harrop
 * @since 2.0
 */
public interface AttributeAccessor {

	/**
	 * 设置属性值
	 */
	void setAttribute(String name, Object value);

	/**
	 * 根据属性名获取属性值
	 */
	Object getAttribute(String name);

	/**
	 * 根据名称移除属性值
	 */
	Object removeAttribute(String name);

	/**
	 * 是否拥有该属性
	 */
	boolean hasAttribute(String name);

	/**
	 * 返回所有的属性名
	 */
	String[] attributeNames();

}
