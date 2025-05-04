/*
 * Copyright © 2025 Cask Data, Inc.
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

package io.cdap.directives.aggregates;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates byte size and time duration columns and outputs a summary row.
 */
@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Description("Aggregates byte size and time duration.")
public class AggregateDirective implements Directive {

  private String byteSizeColumn;
  private String timeDurationColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;

  private long totalBytes = 0;
  private long totalNanos = 0;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
    builder.define("byteSizeCol", TokenType.COLUMN_NAME);
    builder.define("timeDurationCol", TokenType.COLUMN_NAME);
    builder.define("totalSizeCol", TokenType.COLUMN_NAME);
    builder.define("totalTimeCol", TokenType.COLUMN_NAME);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) {
    byteSizeColumn = args.value("byteSizeCol").toString();
    timeDurationColumn = args.value("timeDurationCol").toString();
    totalSizeColumn = args.value("totalSizeCol").toString();
    totalTimeColumn = args.value("totalTimeCol").toString();
  }

@Override
public List<Row> execute(List<Row> rows, ExecutorContext context) {
  for (Row row : rows) {
    Object sizeVal = row.getValue(byteSizeColumn);
    Object timeVal = row.getValue(timeDurationColumn);

    if (sizeVal instanceof Number) {
      totalBytes += ((Number) sizeVal).longValue();
    }

    if (timeVal instanceof Number) {
      totalNanos += ((Number) timeVal).longValue();
    }
  }

  // Prepare result row
  Row result = new Row();
  result.add(totalSizeColumn, totalBytes); 
  result.add(totalTimeColumn, totalNanos); 


  return Collections.singletonList(result); // return result;
}



  public List<Row> finalize(ExecutorContext context) {
    Row result = new Row();
    result.add(totalSizeColumn, totalBytes);
    result.add(totalTimeColumn, totalNanos);
    return Collections.singletonList(result);
  }

  @Override
  public void destroy() {
    // nothing to clean up
  }
}

