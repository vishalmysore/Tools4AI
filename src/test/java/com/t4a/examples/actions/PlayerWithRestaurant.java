package com.t4a.examples.actions;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.examples.basic.RestaurantPojo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Agent
public class PlayerWithRestaurant  {
    @Getter
    private Player player;
    @Getter
    private RestaurantPojo restaurantPojo;
    @Action( description = "add restaurant and player details")
    public String notifyPlayerAndRestaurant(Player player, RestaurantPojo restaurantPojo) {
        log.debug(player.toString());
        log.debug(restaurantPojo.toString());
        this.player = player;
        this.restaurantPojo = restaurantPojo;
        return "yes i have notified";
    }
}
