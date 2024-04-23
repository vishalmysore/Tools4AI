package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import com.t4a.examples.pojo.MyDiary;

@Predict
public class MyDiaryAction {
    @Action(description = "This is my diary details")
    public MyDiary buildMyDiary(MyDiary diary) {
        //take whatever action you want to take
        return diary;
    }
}
