package com.t4a.detect;

public interface ActionCallback {
    void setContext(Object obj);
    Object getContext();
    void sendtStatus(String status,ActionState state);


}
