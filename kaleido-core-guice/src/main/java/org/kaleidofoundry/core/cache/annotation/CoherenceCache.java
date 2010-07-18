/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.cache.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.Coherence35xCacheImpl;

import com.google.inject.BindingAnnotation;

/**
 * Guice binding annotation for binding a {@link Cache} to {@link Coherence35xCacheImpl}
 * 
 * @author Jerome RADUGET
 */
@Target( { FIELD, PARAMETER })
@Retention(RUNTIME)
@BindingAnnotation
public @interface CoherenceCache {
   /** @return cache name */
   String name();
}
