package com.t4a.examples.pojo;

import com.t4a.annotations.ListType;
import com.t4a.examples.actions.Customer;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Organization {
    String name;
    @ListType(Employee.class)
    List<Employee> em;
    @ListType(String.class)
    List<String> locations;
    Customer[] customers;
}
