/*
 * Copyright 2002-2015 the original author or authors.
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
 * 别名注册接口
 * @author lishangang
 * @since 4.3
 */
public interface AliasRegistry {

	/**
	 * 给一个名称注册别名
	 */
	void registerAlias(String name, String alias);

	/**
	 * 从注册表中移除指定别名
	 */
	void removeAlias(String alias);

	/**
	 * 判断指定的名称是否被定义为别名
	 */
	boolean isAlias(String name);

	/**
	 * 根据指定名称，返回其别名列表
	 */
	String[] getAliases(String name);

}
