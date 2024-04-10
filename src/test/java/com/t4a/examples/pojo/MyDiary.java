package com.t4a.examples.pojo;

import com.t4a.annotations.Prompt;
import com.t4a.examples.actions.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MyDiary {
    @Prompt(dateFormat = "ddMMyyyy")
    Date[] allTheDatesOfAppointment;
    String[] friendsNames;
    Customer customer;
    Employee employee;
}
