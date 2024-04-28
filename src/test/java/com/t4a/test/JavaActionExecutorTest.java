package com.t4a.test;

import com.google.cloud.vertexai.api.Type;
import com.google.gson.Gson;
import com.t4a.api.JavaActionExecutor;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class JavaActionExecutorTest {

    // Create a concrete subclass of JavaActionExecutor for testing
    private static class TestJavaActionExecutor extends JavaActionExecutor {
        @Override
        public Map<String, Object> getProperties() {
            return null;
        }

        @Override
        public Gson getGson() {
            return null;
        }
    }



     @Test
      void testMapType() {
         JavaActionExecutor executor = new JavaActionExecutor() {
             @Override
             public Map<String, Object> getProperties() {
                 return null;
             }

             @Override
             public Gson getGson() {
                 return null;
             }
         };

         assertEquals(Type.STRING, executor.mapType("String"));
         assertEquals(Type.INTEGER, executor.mapType("int"));
         assertEquals(Type.INTEGER, executor.mapType("integer"));
         assertEquals(Type.NUMBER, executor.mapType("num"));
         assertEquals(Type.BOOLEAN, executor.mapType("boolean"));
         assertEquals(Type.ARRAY, executor.mapType("array"));
         assertEquals(Type.OBJECT, executor.mapType("object"));
     }

     @Test
     void testMapTypeForPojo() {
         TestJavaActionExecutor executor = new TestJavaActionExecutor();

         assertEquals(Type.STRING, executor.mapTypeForPojo(String.class));
         assertEquals(Type.INTEGER, executor.mapTypeForPojo(int.class));
         assertEquals(Type.INTEGER, executor.mapTypeForPojo(Integer.class));
         assertEquals(Type.NUMBER, executor.mapTypeForPojo(double.class));
         assertEquals(Type.NUMBER, executor.mapTypeForPojo(Double.class));
         assertEquals(Type.BOOLEAN, executor.mapTypeForPojo(boolean.class));
         assertEquals(Type.BOOLEAN, executor.mapTypeForPojo(Boolean.class));
         assertEquals(Type.ARRAY, executor.mapTypeForPojo(new int[]{}.getClass()));
         assertEquals(Type.STRING, executor.mapTypeForPojo(Date.class));
         assertEquals(Type.OBJECT, executor.mapTypeForPojo(Object.class));
     }
}
