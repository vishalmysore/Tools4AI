package com.t4a.test;

import com.t4a.annotations.*;
import java.util.*;

public class CoverageComplexObject {
    @Prompt(describe = "A string field", dateFormat = "yyyy-MM-dd")
    public String stringField;
    public int intField;
    @Prompt(dateFormat = "yyyy-MM-dd")
    public Date dateField;
    public CoverageNestedObject nested;
    public int[] intArray;
    public String[] stringArray;

    @ListType(String.class)
    public List<String> stringList;

    @MapKeyType(String.class)
    @MapValueType(Integer.class)
    public Map<String, Integer> mapField;

    public CoverageComplexObject() {
    }
}
