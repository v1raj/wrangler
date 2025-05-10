/*
 * Copyright © 2025 <Your Organization or Name>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */






package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token representing a time duration (e.g., 150ms, 3h).
 */

public class TimeDuration implements Token {
  private final long millis;

  // Regex to capture value + unit, e.g. 150ms
  private static final Pattern PATTERN = Pattern.compile(
    "^([0-9]+(?:\\.[0-9]+)?)\\s*(MS|S|M|H)$", 
    Pattern.CASE_INSENSITIVE);



  public TimeDuration(String value) {
    Matcher matcher = PATTERN.matcher(value.trim());
    if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    String numberStr = matcher.group(1);
    String unitGroup = matcher.group(2);

    if (numberStr == null || unitGroup == null) {
        throw new IllegalArgumentException("Missing number or unit in time duration format: " + value);
    }

    double number = Double.parseDouble(numberStr);
    String unit = unitGroup.toLowerCase();

    switch (unit) {
        case "s":
            millis = (long) (number * 1000);
            break;
        case "ms":
            millis = (long) number;
            break;
        case "min":
            millis = (long) (number * 60 * 1000);
            break;
        case "hr":
            millis = (long) (number * 60 * 60 * 1000);
            break;
        default:
            throw new IllegalArgumentException("Unsupported time duration unit: " + unit);
    }
}


  public long getMillis() {
    return millis;
  }

  @Override
  public Object value() {
    return millis;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(millis);
  }
}

