package com.t4a.examples;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

    public class SingletonResetter {

        public static void resetSingleton(Class<?> singletonClass) throws Exception {
            // Access the private constructor
            Constructor<?> constructor = singletonClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            // Reset the instance to null
            Field instanceField = singletonClass.getDeclaredField("predictionLoader");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        }
    }


