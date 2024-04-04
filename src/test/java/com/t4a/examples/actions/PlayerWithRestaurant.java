package com.t4a.examples.actions;

import com.t4a.api.JavaMethodAction;
import com.t4a.examples.basic.RestaurantPojo;
import com.t4a.predict.Predict;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Predict(actionName = "notifyPlayerAndRestaurant", description = "add restaurant and player details")
public class PlayerWithRestaurant implements JavaMethodAction {
    @Getter
    private Player player;
    @Getter
    private RestaurantPojo restaurantPojo;
    public String notifyPlayerAndRestaurant(Player player, RestaurantPojo restaurantPojo) {
        log.debug(player.toString());
        log.debug(restaurantPojo.toString());
        this.player = player;
        this.restaurantPojo = restaurantPojo;
        return "yes i have notified";
    }
}
