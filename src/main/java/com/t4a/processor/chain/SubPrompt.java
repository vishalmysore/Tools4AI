package com.t4a.processor.chain;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class SubPrompt {
    private String id;
    private String subprompt;
    private String depend_on;

    @Setter
    @Getter
    boolean processed;
    @Setter
    @Getter
    String actionName;
    @Setter
    @Getter
    String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubprompt() {
        return subprompt;
    }

    public void setSubprompt(String subprompt) {
        this.subprompt = subprompt;
    }

    public String getDepend_on() {
        return depend_on;
    }

    public void setDepend_on(String depend_on) {
        this.depend_on = depend_on;
    }

    public boolean canBeExecutedParallely() {
        return ((depend_on == null)||(depend_on.trim().length() <1));
    }

    @Override
    public String toString() {
        return "SubPrompt{" +
                "id='" + id + '\'' +
                ", subprompt='" + subprompt + '\'' +
                ", depend_on='" + depend_on + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubPrompt subPrompt = (SubPrompt) o;
        return Objects.equals(id, subPrompt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}