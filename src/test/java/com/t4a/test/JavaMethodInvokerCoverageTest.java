package com.t4a.test;

import com.t4a.api.JavaMethodInvoker;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class JavaMethodInvokerCoverageTest {

    @Test
    public void testParseSimple() {
        JavaMethodInvoker invoker = new JavaMethodInvoker();
        String json = "{" +
                "\"methodName\": \"testMethod\"," +
                "\"returnType\": \"void\"," +
                "\"parameters\": [" +
                "  { \"type\": \"String\", \"fieldValue\": \"hello\", \"name\": \"p1\" }," +
                "  { \"type\": \"int\", \"fieldValue\": \"123\", \"name\": \"p2\" }" +
                "]" +
                "}";

        Object[] result = invoker.parse(json);
        Assertions.assertEquals(2, result.length);
        List<Class<?>> types = (List<Class<?>>) result[0];
        List<Object> values = (List<Object>) result[1];

        Assertions.assertEquals(String.class, types.get(0));
        Assertions.assertEquals("hello", values.get(0));

        Assertions.assertEquals(int.class, types.get(1));
        Assertions.assertEquals(123, values.get(1));
    }

    @Test
    public void testParseArray() {
        JavaMethodInvoker invoker = new JavaMethodInvoker();
        // construct valid json for array
        // getType expects "className" for array components if it ends with []
        String json = "{" +
                "\"methodName\": \"testArray\"," +
                "\"returnType\": \"void\"," +
                "\"parameters\": [" +
                "  { \"type\": \"String[]\", \"fieldValue\": [\"a\", \"b\"], \"name\": \"args\", \"className\": \"java.lang.String\" }"
                +
                "]" +
                "}";

        Object[] result = invoker.parse(json);
        List<Class<?>> types = (List<Class<?>>) result[0];
        List<Object> values = (List<Object>) result[1];

        Assertions.assertEquals(1, types.size());
        Assertions.assertTrue(types.get(0).isArray());
        Assertions.assertEquals(String[].class, types.get(0));

        String[] arr = (String[]) values.get(0);
        Assertions.assertEquals(2, arr.length);
        Assertions.assertEquals("a", arr[0]);
        Assertions.assertEquals("b", arr[1]);
    }
}
