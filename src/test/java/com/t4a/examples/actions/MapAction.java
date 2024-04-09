package com.t4a.examples.actions;

import com.t4a.annotations.MapKeyType;
import com.t4a.annotations.MapValueType;
import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Predict(actionName = "addSports",description = "add new Sports into the map")
public class MapAction implements JavaMethodAction {



    public Map<Integer,String> addSports(@MapKeyType(Integer.class)  @MapValueType(String.class) Map<Integer,String> mapOfSportsName) {

        return mapOfSportsName;
    }
}
