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

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SystemPropertyUtils;

/**
 * 抽象属性解析器，实现可配置的属性解析器的部分功能
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {

	protected final Log logger = LogFactory.getLog(getClass());

	// 转换器
	protected ConfigurableConversionService conversionService = new DefaultConversionService();

	private PropertyPlaceholderHelper nonStrictHelper;

	private PropertyPlaceholderHelper strictHelper;

	// 忽略不解析嵌套的占位符
	private boolean ignoreUnresolvableNestedPlaceholders = false;

	// 占位符前缀"${"
	private String placeholderPrefix = SystemPropertyUtils.PLACEHOLDER_PREFIX;

	// 占位符后缀"}"
	private String placeholderSuffix = SystemPropertyUtils.PLACEHOLDER_SUFFIX;

	// value值分隔符":"
	private String valueSeparator = SystemPropertyUtils.VALUE_SEPARATOR;

	// 必须的属性集合，下面必定有add值的地方
	private final Set<String> requiredProperties = new LinkedHashSet<String>();


	/**
	 * 获取转换器
	 */
	@Override
	public ConfigurableConversionService getConversionService() {
		return this.conversionService;
	}

	/**
	 * 设置转换器
	 */
	@Override
	public void setConversionService(ConfigurableConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * 设置占位符前缀
	 */
	@Override
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * 设置占位符后缀
	 */
	@Override
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * 设置value值分隔符
	 */
	@Override
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * 设置是否忽略不解析嵌套占位符
	 */
	@Override
	public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
		this.ignoreUnresolvableNestedPlaceholders = ignoreUnresolvableNestedPlaceholders;
	}

	/**
	 * 设置必须的属性
	 */
	@Override
	public void setRequiredProperties(String... requiredProperties) {
		for (String key : requiredProperties) {
			this.requiredProperties.add(key);
		}
	}

	/**
	 * 校验必须的属性，是否都有属性值，如果有至少一个属性没有属性值，则抛出异常MissingRequiredPropertiesException
	 */
	@Override
	public void validateRequiredProperties() {
		MissingRequiredPropertiesException ex = new MissingRequiredPropertiesException();
		for (String key : this.requiredProperties) {
			if (this.getProperty(key) == null) {
				ex.addMissingRequiredProperty(key);
			}
		}
		if (!ex.getMissingRequiredProperties().isEmpty()) {
			throw ex;
		}
	}

	/**
	 * 是否包含指定属性，如果属性值不为null，返回true，否则false
	 */
	@Override
	public boolean containsProperty(String key) {
		return (getProperty(key) != null);
	}

	/**
	 * 获取属性值，这个需要子类重写<T> T getProperty(String key, Class<T> targetType)
	 */
	@Override
	public String getProperty(String key) {
		return getProperty(key, String.class);
	}

	/**
	 * 获取指定属性，如果没有则返回默认值
	 */
	@Override
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return (value != null ? value : defaultValue);
	}

	/**
	 * 获取指定属性，如果有，则转换为指定类型返回，如果没有，则返回默认值
	 */
	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		T value = getProperty(key, targetType);
		return (value != null ? value : defaultValue);
	}

	@Override
	@Deprecated
	public <T> Class<T> getPropertyAsClass(String key, Class<T> targetValueType) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 获取指定的必须属性的属性值
	 */
	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		String value = getProperty(key);
		if (value == null) {
			throw new IllegalStateException(String.format("required key [%s] not found", key));
		}
		return value;
	}

	/**
	 * 返回与给定键关联的属性值，可转换成指定类型，没有则抛出资格不合法异常IllegalStateException异常
	 */
	@Override
	public <T> T getRequiredProperty(String key, Class<T> valueType) throws IllegalStateException {
		T value = getProperty(key, valueType);
		if (value == null) {
			throw new IllegalStateException(String.format("required key [%s] not found", key));
		}
		return value;
	}

	/**
	 * 替换属性值中的占位符${...}，当无法替换时，保持原样返回
	 */
	@Override
	public String resolvePlaceholders(String text) {
		if (this.nonStrictHelper == null) {
			this.nonStrictHelper = createPlaceholderHelper(true);
		}
		return doResolvePlaceholders(text, this.nonStrictHelper);
	}

	/**
	 * 替换属性值中的占位符${...}，包括嵌套的，当无法替换时，抛出参数不合法异常
	 */
	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		if (this.strictHelper == null) {
			this.strictHelper = createPlaceholderHelper(false);
		}
		return doResolvePlaceholders(text, this.strictHelper);
	}

	/**
	 * 替换属性值中的占位符${...}
	 */
	protected String resolveNestedPlaceholders(String value) {
		return (this.ignoreUnresolvableNestedPlaceholders ?
				resolvePlaceholders(value) : resolveRequiredPlaceholders(value));
	}

	/**
	 * 创建占位符处理器
	 * @param ignoreUnresolvablePlaceholders
	 * @return
	 */
	private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
		return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix,
				this.valueSeparator, ignoreUnresolvablePlaceholders);
	}

	/**
	 * 对占位符进行处理
	 * @param text
	 * @param helper
	 * @return
	 */
	private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
		return helper.replacePlaceholders(text, new PropertyPlaceholderHelper.PlaceholderResolver() {
			@Override
			public String resolvePlaceholder(String placeholderName) {
				return getPropertyAsRawString(placeholderName);
			}
		});
	}


	/**
	 * Retrieve the specified property as a raw String,
	 * i.e. without resolution of nested placeholders.
	 * @param key the property name to resolve
	 * @return the property value or {@code null} if none found
	 */
	protected abstract String getPropertyAsRawString(String key);

}
