package com.t4a.examples.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Activity {
    String dayOfTheWeek;
    String activityName;
}
