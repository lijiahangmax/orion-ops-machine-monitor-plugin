/*
 * Copyright (c) 2021 - present Jiahang Li (om.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.plugin.common.annotation;

import cn.orionsec.ops.plugin.common.interceptor.LogPrintInterceptor;

import java.lang.annotation.*;

/**
 * 不执行统一日志打印
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see LogPrintInterceptor
 * @since 2022/4/20 10:33
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreLog {
}