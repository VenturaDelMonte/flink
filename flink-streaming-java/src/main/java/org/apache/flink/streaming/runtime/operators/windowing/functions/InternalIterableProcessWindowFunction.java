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

package org.apache.flink.streaming.runtime.operators.windowing.functions;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.IterationRuntimeContext;
import org.apache.flink.api.common.functions.RichFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.functions.util.FunctionUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.operators.OutputTypeConfigurable;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

/**
 * Internal window function for wrapping a {@link ProcessWindowFunction} that takes an {@code Iterable}
 * when the window state also is an {@code Iterable}.
 */
@Internal
public final class InternalIterableProcessWindowFunction <IN, OUT, KEY, W extends Window>
		extends InternalWindowFunction<Iterable<IN>, OUT, KEY, W>
		implements RichFunction {

	private static final long serialVersionUID = 1L;

	protected final ProcessWindowFunction<IN, OUT, KEY, W> wrappedFunction;

	public InternalIterableProcessWindowFunction(ProcessWindowFunction<IN, OUT, KEY, W> wrappedFunction) {
		this.wrappedFunction = wrappedFunction;
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		FunctionUtils.openFunction(this.wrappedFunction, parameters);
	}

	@Override
	public void close() throws Exception {
		FunctionUtils.closeFunction(this.wrappedFunction);
	}

	@Override
	public RuntimeContext getRuntimeContext() {
		throw new RuntimeException("This should never be called.");
	}

	@Override
	public IterationRuntimeContext getIterationRuntimeContext() {
		throw new RuntimeException("This should never be called.");
	}

	@Override
	public void setRuntimeContext(RuntimeContext t) {
		FunctionUtils.setFunctionRuntimeContext(this.wrappedFunction, t);
	}

	@Override
	public void apply(KEY key, W window, Iterable<IN> input, Collector<OUT> out) throws Exception {
		throw new RuntimeException("This should never be called.");
	}


	@Override
	public void apply(final KEY key,
						final W window,
						final Iterable<IN> input,
						final Collector<OUT> out,
						final long firingCounter) throws Exception {

		wrappedFunction.process(wrappedFunction.new Context() {
			@Override
			public KEY key() {
				return key;
			}

			@Override
			public W window() {
				return window;
			}

			@Override
			public Iterable<IN> elements() {
				return input;
			}

			@Override
			public void output(OUT value) {
				out.collect(value);
			}

			@Override
			public long id() {
				return firingCounter;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setOutputType(TypeInformation<OUT> outTypeInfo, ExecutionConfig executionConfig) {
		if (OutputTypeConfigurable.class.isAssignableFrom(this.wrappedFunction.getClass())) {
			((OutputTypeConfigurable<OUT>)this.wrappedFunction).setOutputType(outTypeInfo, executionConfig);
		}
	}
}
