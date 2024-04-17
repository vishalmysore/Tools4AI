package com.t4a.examples.pojo;

import com.t4a.annotations.ListType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MyGymSchedule {
  @ListType(Activity.class)
  List<Activity> myWeeklyActivity;

}
