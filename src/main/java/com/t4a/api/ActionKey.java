package com.t4a.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


@Getter
@Setter
@NoArgsConstructor
public class ActionKey {
    private String actionName;
    private String actionDescription;
    private long uniqueKey ;
    public ActionKey(AIAction action) {
        generateUniqueKey();
        actionName = action.getActionName();
        actionDescription = action.getDescription();
    }

    private void generateUniqueKey() {
        AtomicLong counter = new AtomicLong(System.currentTimeMillis());
        uniqueKey = counter.incrementAndGet();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionKey actionKey = (ActionKey) o;
        return Objects.equals(uniqueKey, actionKey.uniqueKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueKey);
    }
}
