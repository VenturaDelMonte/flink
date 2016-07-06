/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.api.functions.windowing;

import org.apache.flink.annotation.Public;
import org.apache.flink.api.common.functions.Function;
import org.apache.flink.streaming.api.windowing.windows.Window;

/**
 * Base abstract class for functions that are evaluated over keyed (grouped) windows using a context
 * for passing extra information.
 *
 * @param <IN> The type of the input value.
 * @param <OUT> The type of the output value.
 * @param <KEY> The type of the key.
 * @param <W> The type of {@code Window} that this window function can be applied on.
 */
@Public
public abstract class ProcessWindowFunction <IN, OUT, KEY, W extends Window> implements Function {

	private static final long serialVersionUID = 1L;

	/**
	 * Evaluates the window and outputs none or several elements.
	 * A Context {@link ProcessWindowFunction.Context} holding basic function arguments (input, output, key,
	 * window) and the window firing counter is passed as main argument.
	 * @param c the context holding window information
	 * @throws Exception
	 */
	public abstract void process(Context c) throws Exception;

	/**
	 * The context holding basic function arguments (input, output, key, window) and the firing window counter.
	 */
	public abstract class Context {

		/**
		 * @return The key for which this window is evaluated.
		 */
		public abstract KEY key();
		/**
		 * @return The window that is being evaluated.
		 */
		public abstract W window();
		/**
		 * @return The elements in the window being evaluated.
		 */
		public abstract Iterable<IN> elements();
		/**
		 * @return A collector for emitting elements.
		 */
		public abstract void output(OUT value);

		/**
		 * @return the id/count of the current window firing
		 */
		public abstract long id();
	}
}