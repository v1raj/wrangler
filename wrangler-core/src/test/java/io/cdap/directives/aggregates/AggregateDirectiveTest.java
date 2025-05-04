
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


import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;





public class AggregateDirectiveTest {

    @Test
    public void testAggregateStats() throws Exception {
   
        Row row1 = new Row().add("data_transfer_size", "1000B").add("response_time", "100ms");
        Row row2 = new Row().add("data_transfer_size", "2000B").add("response_time", "200ms");
        Row row3 = new Row().add("data_transfer_size", "3000B").add("response_time", "300ms");

        List<Row> rows = List.of(row1, row2, row3);

        String[] recipe = new String[] {
            "#pragma version 2.0;",
            "aggregate-stats :data_transfer_size :response_time :total_size_mb :total_time_sec"
        };

        List<Row> results = TestingRig.execute(recipe, rows);
       
        double expectedTotalSizeInMB = 6000;
        
        double expectedTotalTimeInSeconds = 600;

    
        Assert.assertEquals(expectedTotalSizeInMB, (Double) results.get(0).getValue("total_size_mb"), 0.001);
        Assert.assertEquals(expectedTotalTimeInSeconds, (Double) results.get(0).getValue("total_time_sec"), 0.001);
    }
}
