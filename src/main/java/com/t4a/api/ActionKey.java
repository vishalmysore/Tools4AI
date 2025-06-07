package com.t4a.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ActionKey {
    private String actionName;
    private String actionDescription;
    private long uniqueKey ;    public ActionKey(AIAction action) {
        generateUniqueKey();
        if (action != null) {
            actionName = action.getActionName();
            actionDescription = action.getDescription();
        }
    }

    private void generateUniqueKey() {
        UUID uuid = UUID.randomUUID();
        uniqueKey = uuid.getMostSignificantBits() & Long.MAX_VALUE;

    }    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionKey actionKey = (ActionKey) o;
        return uniqueKey == actionKey.uniqueKey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueKey);
    }
}
