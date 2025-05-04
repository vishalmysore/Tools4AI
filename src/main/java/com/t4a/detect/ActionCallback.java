package com.t4a.detect;

public interface ActionCallback {
    void setContext(Object obj);
    Object getContext();
    String getType();
    String setType(String type);
    void sendtStatus(String status,ActionState state);


}
