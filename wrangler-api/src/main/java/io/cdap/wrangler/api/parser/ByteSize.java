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
 * Token representing a byte size (e.g., 10KB, 5MB).
 */

public class ByteSize implements Token {
  private final long bytes;

  // Regex to capture value + unit, e.g. 10KB
  private static final Pattern PATTERN = Pattern.compile(
    "^([0-9]+(?:\\.[0-9]+)?)\\s*(B|KB|MB|GB|TB|PB)$", 
    Pattern.CASE_INSENSITIVE);


  public ByteSize(String value) {
    Matcher matcher = PATTERN.matcher(value.trim());
    if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    String numberStr = matcher.group(1);
    String unitGroup = matcher.group(2);

    if (numberStr == null || unitGroup == null) {
        throw new IllegalArgumentException("Missing value or unit in byte size format: " + value);
    }

    double number = Double.parseDouble(numberStr);
    String unit = unitGroup.toUpperCase();

    switch (unit) {
        case "B":
            bytes = (long) number;
            break;
        case "KB":
            bytes = (long) (number * 1024L);
            break;
        case "MB":
            bytes = (long) (number * 1024L * 1024);
            break;
        case "GB":
            bytes = (long) (number * 1024L * 1024 * 1024);
            break;
        case "TB":
            bytes = (long) (number * 1024L * 1024 * 1024 * 1024);
            break;
        case "PB":
            bytes = (long) (number * 1024L * 1024 * 1024 * 1024 * 1024);
            break;
        default:
            throw new IllegalArgumentException("Unsupported byte size unit: " + unit);
    }
}


  public long getBytes() {
    return bytes;
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(bytes);
  }
}
