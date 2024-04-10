package com.t4a.examples.actions;

import com.t4a.annotations.Predict;
import com.t4a.api.JavaMethodAction;
import com.t4a.examples.pojo.MyDiary;

@Predict(actionName = "buildMyDiary" , description = "This is my diary details")
public class MyDiaryAction implements JavaMethodAction {
    public MyDiary buildMyDiary(MyDiary diary) {
        //take whatever action you want to take
        return diary;
    }
}
