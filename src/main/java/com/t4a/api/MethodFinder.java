package com.t4a.api;

import com.t4a.processor.AIProcessingException;

import java.lang.reflect.Method;

public interface MethodFinder {
    Method findMethod(Class<?> clazz, String methodName) throws AIProcessingException;
}
