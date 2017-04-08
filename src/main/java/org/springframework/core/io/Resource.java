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

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * 资源属性的相关信息
 * @author lishangang
 * @since 4.3
 */
public interface Resource extends InputStreamSource {

	/**
	 * 判断资源是否存在
	 */
	boolean exists();

	/**
	 * 是否可读
	 */
	boolean isReadable();

	/**
	 * 指示此资源是否表示具有打开流的句柄。
	 * 无法读取InputStream的多次，并必须予以封闭，以防止资源泄漏。
	 */
	boolean isOpen();

	/**
	 * 获取资源的URL
	 */
	URL getURL() throws IOException;

	/**
	 * 获取资源URI
	 */
	URI getURI() throws IOException;

	/**
	 * 获取资源对应的文件
	 */
	File getFile() throws IOException;

	/**
	 * 确定资源长度
	 */
	long contentLength() throws IOException;

	/**
	 * 确定资源最后修改时间
	 */
	long lastModified() throws IOException;

	/**
	 * 在这个资源路径的相对路径，创建一个资源
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 获取资源的文件名
	 */
	String getFilename();

	/**
	 * 获取资源描述
	 */
	String getDescription();

}
