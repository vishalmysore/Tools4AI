package com.t4a.examples.basic;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.Schema;
import com.google.cloud.vertexai.api.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunctionHelper {
    public static void main(String[] args) {
        FunctionDeclaration functionTaste = FunctionDeclaration.newBuilder()
                .setName("RestaurantClass")
                .setDescription("provide the taste of recipe based on name")

                .setParameters(
                        Schema.newBuilder()
                                .setType(Type.OBJECT)
                                .putProperties("name", Schema.newBuilder()
                                        .setType(Type.STRING)
                                        .setDescription("name").build() )
                                .putProperties("RestaurantDetails", Schema.newBuilder()
                                        .setType(Type.OBJECT)
                                        .setDescription("RestaurantDetails")
                                        .putProperties("city", Schema.newBuilder()
                                                .setType(Type.STRING)
                                                .setDescription("city").build() )
                                        .putProperties("location", Schema.newBuilder()
                                                .setType(Type.STRING)
                                                .setDescription("location").build() )
                                        .build()
                                )
                                .addRequired("taste")
                                .build()
                )
                .build();
        log.debug("Function declaration h1:");
        log.debug(""+functionTaste);
        log.debug("Function declaration h2:");
        log.debug(""+functionTaste);

    }
}
