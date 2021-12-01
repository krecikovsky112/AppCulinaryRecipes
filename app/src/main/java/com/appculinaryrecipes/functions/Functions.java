package com.appculinaryrecipes.functions;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Functions {
    public static final String RECIPE_BY_INGREDIENTS_FUNCTION = "getRecipeByindigrients";
    public static final String RECIPE_BY_AREA_AND_CATEGORY_INGREDIENTS_FUNCTION = "getRecipeByAreaAndCategory";
    public static final String INGREDIENTS_ARG = "ingredients";
    public static final String AREA_ARG = "area";
    public static final String CATEGORY_ARG = "category";

    public static Task<String> getRecipesByIngredients(List<String> ingredients) {
        Map<String, Object> data = new HashMap<>();
        data.put(INGREDIENTS_ARG, ingredients);
        data.put("push", true);
        return FirebaseFunctions.getInstance()
                .getHttpsCallable(RECIPE_BY_INGREDIENTS_FUNCTION)
                .call(data)
                .continueWith(task -> (String) task.getResult().getData());
    }

    public static Task<String> getRecipeByAreaAndCategory(String area, String category) {
        Map<String, Object> data = new HashMap<>();
        data.put(AREA_ARG, area);
        data.put(CATEGORY_ARG, category);
        data.put("push", true);
        return FirebaseFunctions.getInstance()
                .getHttpsCallable(RECIPE_BY_AREA_AND_CATEGORY_INGREDIENTS_FUNCTION)
                .call(data)
                .continueWith(task -> (String) task.getResult().getData());
    }
}
