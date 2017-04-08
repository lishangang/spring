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

import org.springframework.core.convert.support.ConfigurableConversionService;

/**
 * 可配置的属性解析器
 * 定义了如何对组件本身进行配置
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

	/**
	 * 获取类型转换器
	 */
	ConfigurableConversionService getConversionService();

	/**
	 * 设置类型转换器
	 */
	void setConversionService(ConfigurableConversionService conversionService);

	/**
	 * 设置占位符前缀
	 */
	void setPlaceholderPrefix(String placeholderPrefix);

	/**
	 * 设置占位符后缀
	 */
	void setPlaceholderSuffix(String placeholderSuffix);

	/**
	 * 设置value值分隔字符串
	 */
	void setValueSeparator(String valueSeparator);

	/**
	 * 是否忽略无法解析的占位符
	 */
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	/**
	 * 设置必须的属性
	 */
	void setRequiredProperties(String... requiredProperties);

	/**
	 * 校验必须的属性，是否都有属性值，如果至少一个属性没有属性值，则抛出异常MissingRequiredPropertiesException
	 */
	void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
