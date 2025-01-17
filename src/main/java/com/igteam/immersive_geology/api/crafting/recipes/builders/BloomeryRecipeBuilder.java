package com.igteam.immersive_geology.api.crafting.recipes.builders;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.common.crafting.Serializers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class BloomeryRecipeBuilder extends IEFinishedRecipe<BloomeryRecipeBuilder> {

    public BloomeryRecipeBuilder() {
        super(Serializers.BLOOMERY_SERIALIZER.get());
    }

    public static BloomeryRecipeBuilder builder(Item result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public static BloomeryRecipeBuilder builder(ItemStack result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public static BloomeryRecipeBuilder builder(ITag<Item> result, int count)
    {
        return new BloomeryRecipeBuilder().addResult(new IngredientWithSize(result, count));
    }

    public static BloomeryRecipeBuilder builder(IngredientWithSize result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public BloomeryRecipeBuilder addItemInput(ItemStack input){
        if(!input.isEmpty()) {
            this.addIngredient("item_input", IngredientWithSize.of(input));
        }
        return this;
    }
}
