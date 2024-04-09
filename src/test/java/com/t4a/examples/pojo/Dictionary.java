package com.t4a.examples.pojo;

import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Dictionary {
    String nameOfDictionary;
    @MapValueType(String.class)
    @MapKeyType(String.class)
    Map<String,String> wordMeanings;
    String locations[];
}
