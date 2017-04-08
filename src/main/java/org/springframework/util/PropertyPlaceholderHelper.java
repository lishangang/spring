/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 属性占位符处理器
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 3.0
 */
public class PropertyPlaceholderHelper {

	private static final Log logger = LogFactory.getLog(PropertyPlaceholderHelper.class);

	private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<String, String>(4);

	static {
		wellKnownSimplePrefixes.put("}", "{");
		wellKnownSimplePrefixes.put("]", "[");
		wellKnownSimplePrefixes.put(")", "(");
	}


	// 占位符前缀
	private final String placeholderPrefix;

	// 占位符后缀
	private final String placeholderSuffix;

	// 常见通用的前缀
	private final String simplePrefix;

	// value分隔符
	private final String valueSeparator;

	// 如果找不到占位符对应的属性值，是否忽略处理该占位符
	private final boolean ignoreUnresolvablePlaceholders;


	/**
	 * 构造方法，创建一个忽略无法解析的占位符 的 处理器
	 * @param placeholderPrefix 占位符前缀
	 * @param placeholderSuffix 占位符后缀
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, null, true);
	}

	/**
	 * 构造方法
	 * simplePrefix字段是用来处理嵌套时的影响的，比如${a{}dd},前缀${,后缀},如果没这个字段的参与，返回的后缀的下标很可能就是4，实际应该是7
	 * @param placeholderPrefix 占位符前缀
	 * @param placeholderSuffix 占位符后缀
	 * @param valueSeparator 值的分隔符
	 * @param ignoreUnresolvablePlaceholders 是否忽略占位符嵌套
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix,
			String valueSeparator, boolean ignoreUnresolvablePlaceholders) {

		Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
		Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
		if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
			this.simplePrefix = simplePrefixForSuffix;
		}
		else {
			this.simplePrefix = this.placeholderPrefix;
		}
		this.valueSeparator = valueSeparator;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}


	/**
	 * 将字符串中的占位符替换为对应的属性值
	 * @param value 字符串
	 * @param properties 属性集
	 */
	public String replacePlaceholders(String value, final Properties properties) {
		Assert.notNull(properties, "'properties' must not be null");
		return replacePlaceholders(value, new PlaceholderResolver() {
			@Override
			public String resolvePlaceholder(String placeholderName) {
				return properties.getProperty(placeholderName);
			}
		});
	}

	/**
	 * 将字符串中的占位符替换为对应的属性值
	 * @param value 字符串
	 * @param placeholderResolver 属性解析器
	 */
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		Assert.notNull(value, "'value' must not be null");
		return parseStringValue(value, placeholderResolver, new HashSet<String>());
	}

	/**
	 * 替换指定字符串中占位符，转换为对应的属性值
	 * @param strVal
	 * @param placeholderResolver
	 * @param visitedPlaceholders
	 * @return
	 */
	protected String parseStringValue(
			String strVal, PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {

		StringBuilder result = new StringBuilder(strVal);
		// 获取占位符前缀的下标，如果不包含前缀，则原样返回
		int startIndex = strVal.indexOf(this.placeholderPrefix);
		while (startIndex != -1) {
			// 获取该前缀对应的后缀的下标
			int endIndex = findPlaceholderEndIndex(result, startIndex);
			if (endIndex != -1) {
				// 截取占位符中的数据，这是原始数据，比如${abc}，原始数据就是abc
				String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				String originalPlaceholder = placeholder;
				// 这个set集合的出现，是为了避免占位符死循环的问题，比如，${a} 对应的属性值是 ${b}, 而${b} 对应的属性值又是 ${a}，则容易陷入无限循环之中
				if (!visitedPlaceholders.add(originalPlaceholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
				}
				// 递归调用，直到获取占位符中最有效的数据，比如${{abc}}，获取最终的abc
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
				// 获取占位符内容对应的解析值，如${abc} 对应的是  1
				String propVal = placeholderResolver.resolvePlaceholder(placeholder);
				// 如果没有获取属性值，根据分隔符分隔占位符所在的字符串，即key，一般只有一个分隔符，即a,b的形式，先把a当做属性name去查，如果查询不到，则b就做默认的属性值
				if (propVal == null && this.valueSeparator != null) {
					int separatorIndex = placeholder.indexOf(this.valueSeparator);
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0, separatorIndex);
						String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
						propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// 再次递归调用，可能属性值中仍然包含占位符
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
					// 将占位符替换成属性值 ，如a${abc}b 替换成  a1b
					result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
					// 继续处理字符串中其它的占位符，所以起始下标继续下移
					startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				}
				else if (this.ignoreUnresolvablePlaceholders) {
					// 如果占位符对应的属性值不存在，则忽略处理该占位符，继续处理后续的
					startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				}
				else {
					// 除非ignoreUnresolvablePlaceholders传的值是 null，否则发生不了
					throw new IllegalArgumentException("Could not resolve placeholder '" +
							placeholder + "'" + " in string value \"" + strVal + "\"");
				}
				visitedPlaceholders.remove(originalPlaceholder);
			}
			else {
				startIndex = -1;
			}
		}

		return result.toString();
	}
	
	/**
	 * 获取占位符结束标记的下标
	 * 举例：ab{[{}]}cd，计算与第一个大括号对应的占位符的后缀的下标，返回7
	 * @param buf 包含占位符的字符串
	 * @param startIndex 前缀的下标
	 * @return
	 */
	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					index = index + this.placeholderSuffix.length();
				}
				else {
					return index;
				}
			}
			else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
				withinNestedPlaceholder++;
				index = index + this.simplePrefix.length();
			}
			else {
				index++;
			}
		}
		return -1;
	}


	/**
	 * 用来获取占位符对应的属性值
	 */
	public static interface PlaceholderResolver {

		/**
		 * 将所提供的占位符名称解析为替换值
		 * @param placeholderName 要解析的占位符的名称
		 * @return 返回替换值，如果没替换，则返回null
		 */
		String resolvePlaceholder(String placeholderName);
	}

}
