package com.t4a.examples.pojo;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AutoRepairScreen {
    double fullInspectionValue;
    double tireRotationValue;
    double oilChangeValue;
    Integer phoneNumber;
    String email;
    String[] customerReviews;

}
