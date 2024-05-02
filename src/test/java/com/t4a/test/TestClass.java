package com.t4a.test;

import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import com.t4a.examples.pojo.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public  class TestClass {
    private String name;
    private int age;
    private double salary;
    private boolean isActive;
    private List<String> hobbies;
    private Map<String, Integer> scores;
    private Map<String, Integer> testMap;
    @MapValueType(Integer.class)
    @MapKeyType(String.class)
    private Map<String, Integer> testCustomers;
    // getters and setters
    private int[] intArray;
    private boolean[] booleanArray;
    private double[] doubleArray;
    private String[] stringArray;
    private long[] longArray;
    private Date[] dateArray;
    private List<String> testList;
    private List<Employee> employeeList;
    public void testAddMap(@MapKeyType(String.class) @MapValueType(Integer.class) Map<String, Integer> testMapEmployees) {
        testMapEmployees.put("test", 1);
    }
}

