
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

import io.cdap.cdap.etl.api.Lookup;
import io.cdap.cdap.etl.api.StageMetrics;
import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.TransientStore;
import io.cdap.wrangler.api.TransientVariableScope;
import io.cdap.wrangler.proto.Contexts;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;





public class AggregateDirectiveTest {

    @Test
    public void testAggregateStats() throws Exception {
        
        String[] recipe = new String[]{
      "aggregate-stats :data_transfer_size :response_time :total_size_mb :total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "1000B").add("response_time", "100ms"));
    rows.add(new Row("data_transfer_size", "2000B").add("response_time", "200ms"));
    rows.add(new Row("data_transfer_size", "3000B").add("response_time", "300ms"));

    final Map<String, Object> s = new HashMap<>();
    rows = TestingRig.execute(recipe, rows, new ExecutorContext() {
      @Override
      public Environment getEnvironment() {
        return Environment.TESTING;
      }

      @Override
      public String getNamespace() {
        return Contexts.SYSTEM;
      }

      @Override
      public StageMetrics getMetrics() {
        return null;
      }

      @Override
      public String getContextName() {
        return "test";
      }

      @Override
      public Map<String, String> getProperties() {
        return new HashMap<>();
      }

      @Override
      public URL getService(String applicationId, String serviceId) {
        return null;
      }

      @Override
      public TransientStore getTransientStore() {
        return new TransientStore() {
          @Override
          public void reset(TransientVariableScope scope) {

          }

          @Override
          public <T> T get(String name) {
            return (T) s.get(name);
          }

          @Override
          public void set(TransientVariableScope scope, String name, Object value) {
            s.put(name, value);
          }

          @Override
          public void increment(TransientVariableScope scope, String name, long value) {

          }

          @Override
          public Set<String> getVariables() {
            return s.keySet();
          }
        };
      }

      @Override
      public <T> Lookup<T> provide(String s, Map<String, String> map) {
        return null;
      }
    });


       
        long expectedTotalSizeInMB = 6000;
        
        long expectedTotalTimeInSeconds = 600;
        System.out.println("Total size: " + rows.get(2).getValue("total_size_mb").toString());
     
    
        Assert.assertEquals(expectedTotalSizeInMB, (Long) rows.get(2).getValue("total_size_mb"), 0.001);
        Assert.assertEquals(expectedTotalTimeInSeconds, (Long) rows.get(2).getValue("total_time_sec"), 0.001);
    }
}
