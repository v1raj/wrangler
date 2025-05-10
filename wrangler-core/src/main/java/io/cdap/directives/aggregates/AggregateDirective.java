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
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates byte size and time duration columns and outputs a summary row.
 */
@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Categories(categories = { "Aggregate"})
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
    this.byteSizeColumn = args.value("byteSizeCol").value().toString();
    this.timeDurationColumn = args.value("timeDurationCol").value().toString();
    this.totalSizeColumn = args.value("totalSizeCol").value().toString();
    this.totalTimeColumn = args.value("totalTimeCol").value().toString();
  }
 
  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) {
  for (Row row : rows) {
    //Object sizeVal = row.getValue(this.byteSizeColumn);
    //Object timeVal = row.getValue(this.timeDurationColumn);
    ByteSize b = new ByteSize(row.getValue(this.byteSizeColumn).toString());
    TimeDuration t = new TimeDuration(row.getValue(this.timeDurationColumn).toString());

    System.out.println("Byte size columns:" + this.byteSizeColumn);
    System.out.println("Size Value: " + row.getValue(this.byteSizeColumn));
    
      totalBytes += b.getBytes();

      totalNanos += t.getMillis();
    
  }

  // Prepare result row
  Row result = new Row();
  result.add(totalSizeColumn, totalBytes); 
  result.add(totalTimeColumn, totalNanos); 
  System.out.println("Total Bytes: " + totalBytes + ", Total Nanos: " + totalNanos);

  return Collections.singletonList(result); // return result;
}
  
    
  
 
  @Override
  public void destroy() {
    // nothing to clean up
  }
}

