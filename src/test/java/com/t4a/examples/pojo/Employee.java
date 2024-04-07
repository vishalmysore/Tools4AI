package com.t4a.examples.pojo;

import com.t4a.annotations.Prompt;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    private String name;
    @Prompt(ignore = true)
    private int id;
    private String department;
    private double salary;
    private String location;
    @Prompt(dateFormat = "ddMMyyyy" ,describe = "convert to actual date")
    private Date dateJoined;
    private String[] tasks;
}
