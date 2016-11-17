/**
 * Copyright (C) 2014 Square, Inc.
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


package retrofit2.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query parameter keys and values appended to the URL.
 * <p>
 * Both keys and values are converted to strings using {@link String#valueOf(Object)}.
 * <p>
 * Simple Example:
 * <pre><code>
 * &#64;GET("/search")
 * Call&lt;ResponseBody&gt; list(@QueryMap Map&lt;String, String&gt; filters);
 * </code></pre>
 * Calling with {@code foo.list(ImmutableMap.of("foo", "bar", "kit", "kat"))} yields
 * {@code /search?foo=bar&kit=kat}.
 * <p>
 * Map keys and values representing parameter values are URL encoded by default. Specify
 * {@link #encoded() encoded=true} to change this behavior.
 * <pre><code>
 * &#64;GET("/search")
 * Call&lt;ResponseBody&gt; list(@QueryMap(encoded=true) Map&lt;String, String&gt; filters);
 * </code></pre>
 * Calling with {@code foo.list(ImmutableMap.of("foo", "foo+bar"))} yields
 * {@code /search?foo=foo+bar}.
 * <p>
 * A {@code null} value for the map, as a key, or as a value is not allowed.
 * 
 * @see Query
 */
@Documented
@Target(value = ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface QueryMap {
    /**
     * * Specifies whether parameter names and values are already URL encoded.
     */
    boolean encoded() default false;
}
