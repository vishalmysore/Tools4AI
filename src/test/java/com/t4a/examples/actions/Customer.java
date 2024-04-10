package com.t4a.examples.actions;

import com.t4a.annotations.Prompt;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Customer {
    private String firstName;
    private String lastName;
    @Prompt(describe = "convert this to Hindi")
    private String reasonForCalling;
    @Prompt(ignore = true)
    private String location;
    @Prompt(dateFormat = "yyyy-MM-dd" ,describe = "if you dont find date provide todays date in fieldValue")
    private Date dateJoined;
}
