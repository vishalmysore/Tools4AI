package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.examples.pojo.MyDiary;

@Agent
public class MyDiaryAction {
    @Action(description = "This is my diary details")
    public MyDiary buildMyDiary(MyDiary diary) {
        //take whatever action you want to take
        return diary;
    }
}
