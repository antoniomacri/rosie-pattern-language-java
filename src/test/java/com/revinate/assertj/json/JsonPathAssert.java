/*
The MIT License (MIT)

Copyright (c) 2016 Revinate, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/*
Taken from:
  https://github.com/revinate/assertj-json

because of an incompatibility:
  java.lang.NoSuchMethodError: org.assertj.core.api.Assertions.assertThat(Ljava/lang/String;)Lorg/assertj/core/api/AbstractCharSequenceAssert;
*/

package com.revinate.assertj.json;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import org.assertj.core.api.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Assertions for {@link DocumentContext}.
 *
 * @author Jorge Lee
 */
public class JsonPathAssert extends AbstractAssert<JsonPathAssert, DocumentContext> {

    public JsonPathAssert(DocumentContext actual) {
        super(actual, JsonPathAssert.class);
    }

    public static JsonPathAssert assertThat(DocumentContext documentContext) {
        return new JsonPathAssert(documentContext);
    }

    /**
     * Extracts a JSON text using a JsonPath expression and wrap it in a {@link StringAssert}.
     *
     * @param path JsonPath to extract the string
     * @return an instance of {@link StringAssert}
     */
    public AbstractCharSequenceAssert<?, String> jsonPathAsString(String path) {
        return Assertions.assertThat(actual.read(path, String.class));
    }

    /**
     * Extracts a JSON number using a JsonPath expression and wrap it in an {@link IntegerAssert}
     *
     * @param path JsonPath to extract the number
     * @return an instance of {@link IntegerAssert}
     */
    public AbstractIntegerAssert<?> jsonPathAsInteger(String path) {
        return Assertions.assertThat(actual.read(path, Integer.class));
    }

    /**
     * Extracts a JSON array using a JsonPath expression and wrap it in a {@link ListAssert}. This method requires
     * the JsonPath to be <a href="https://github.com/jayway/JsonPath#jsonprovider-spi">configured with Jackson or
     * Gson</a>.
     *
     * @param path JsonPath to extract the array
     * @param type The type to cast the content of the array, i.e.: {@link String}, {@link Integer}
     * @param <T>  The generic type of the type field
     * @return an instance of {@link ListAssert}
     */
    public <T> AbstractListAssert<?, ? extends List<? extends T>, T, ? extends AbstractAssert<?, T>> jsonPathAsListOf(String path, Class<T> type) {
        return Assertions.assertThat(actual.read(path, new TypeRef<List<T>>() {
        }));
    }

    /**
     * Extracts a JSON number using a JsonPath expression and wrap it in an {@link BigDecimalAssert}
     *
     * @param path JsonPath to extract the number
     * @return an instance of {@link BigDecimalAssert}
     */
    public AbstractBigDecimalAssert<?> jsonPathAsBigDecimal(String path) {
        return Assertions.assertThat(actual.read(path, BigDecimal.class));
    }

    /**
     * Extracts a JSON boolean using a JsonPath expression and wrap it in an {@link BooleanAssert}
     *
     * @param path JsonPath to extract the number
     * @return an instance of {@link BooleanAssert}
     */
    public AbstractBooleanAssert<?> jsonPathAsBoolean(String path) {
        return Assertions.assertThat(actual.read(path, Boolean.class));
    }

    /**
     * Extracts a any JSON type using a JsonPath expression and wrap it in an {@link ObjectAssert}. Use this method
     * to check for nulls or to do type checks.
     *
     * @param path JsonPath to extract the type
     * @return an instance of {@link ObjectAssert}
     */
    public AbstractObjectAssert<?, Object> jsonPath(String path) {
        return Assertions.assertThat(actual.read(path, Object.class));
    }
}
