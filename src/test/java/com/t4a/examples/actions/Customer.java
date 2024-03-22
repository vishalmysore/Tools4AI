package com.t4a.examples.actions;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Customer {
    private String firstName;
    private String lastName;
    private String reasonForCalling;
    private String location;
}
