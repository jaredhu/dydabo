/*******************************************************************************
 * Copyright 2017 viswadas leher <vleher@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************************/
package com.dydabo.blackbox.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author viswadas leher <vleher@gmail.com>
 */
public class DyDaBoUtils {

    /**
     *
     */
    public static final String EMPTY_ARRAY = "[]";

    /**
     *
     */
    public static final String EMPTY_MAP = "{}";

    /**
     *
     * @param str
     *
     * @return
     */
    public static boolean isBlankOrNull(String... str) {
        for (String s : str) {
            if (s == null || s.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param regexValue
     *
     * @return
     */
    public static boolean isValidRegex(String regexValue) {

        if (isBlankOrNull(regexValue)) {
            return false;
        }

        if (EMPTY_ARRAY.equals(regexValue)) {
            return false;
        }

        if (EMPTY_MAP.equals(regexValue)) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param jsonString
     *
     * @return
     */
    public static JsonElement parseJsonString(String jsonString) {
        try {
            JsonElement elem = new JsonParser().parse(jsonString);
            return elem;
        } catch (JsonSyntaxException ex) {
            // ignore for now
        } catch (NullPointerException ex) {
            // ignore for now
        }

        return null;
    }

    /**
     *
     *
     * @param obj
     *
     * @return the boolean
     */
    public static boolean isPrimitiveOrPrimitiveWrapperOrString(Object obj) {
        Class<?> type = obj.getClass();
        return (type.isPrimitive() && type != void.class) || type == Double.class || type == Float.class || type == Long.class || type == Integer.class || type == Short.class || type == Character.class || type == Byte.class || type == Boolean.class || type == String.class;
    }

}
