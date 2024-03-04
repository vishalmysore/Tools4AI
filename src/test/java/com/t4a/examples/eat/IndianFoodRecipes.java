package com.t4a.examples.eat;

import java.util.HashMap;
import java.util.Map;

public class IndianFoodRecipes {
    public static Map<String, Object> getRecipe(){
        System.out.println("creating collection");
        Map<String, Object> recipeTastes = new HashMap<>();

        recipeTastes.put("Aloo Gobi", "Spicy & Tangy");
        recipeTastes.put("Butter Chicken", "Rich & Creamy");
        recipeTastes.put("Chicken Tikka Masala", "Tangy & Flavorful");
        recipeTastes.put("Chhole Bhature", "Spicy & Savory");
        recipeTastes.put("Daal Makhani", "Rich & Creamy");
        recipeTastes.put("Dhokla", "Savory & Slightly Spiced");
        recipeTastes.put("Idli Sambar", "Savory & Light");
        // ... (add more recipes)
        recipeTastes.put("Samosa Chaat", "Savory & Tangy");
        recipeTastes.put("Upma", "Savory & Warm");
        recipeTastes.put("Vada Pav", "Spicy & Flavorful");
        return recipeTastes;
    }

    public static Map<String, Object> getHealthy(){
        System.out.println("creating collection for health");
        Map<String, Object> recipeTastes = new HashMap<>();

        recipeTastes.put( "Spicy & Tangy","No, Spicy food might not be good");
        recipeTastes.put( "Rich & Creamy","No, its not very healthy");
        recipeTastes.put( "Tangy & Flavorful","Yes, Its so good to eat Tangy food");
        recipeTastes.put( "Spicy & Savory","Yes, Spicy with Savory is the best option");
        recipeTastes.put( "Savory & Slightly Spiced","Yes");
        recipeTastes.put( "Savory & Light","Yes, its so healthy");
        // ... (add more recipes)
        recipeTastes.put( "Savory & Tangy","No, savory and tangy is not healthy option");
        recipeTastes.put( "Savory & Warm","Yes, Warm is good for health");
        recipeTastes.put( "Spicy & Flavorful","No, Too much flavor is bad , you should eat bland food");
        return recipeTastes;
    }
}
