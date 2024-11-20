package com.example.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.data.RecipeWithDetails;
import com.example.models.Ingredient;
import com.example.models.Instruction;
import com.example.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE title = :title")
    Recipe getRecipeByTitle(String title);

    @Transaction
    @Query("SELECT * FROM recipes WHERE title = :title")
    RecipeWithDetails getRecipeWithDetailsByTitle(String title);

    @Query("SELECT COUNT(*) FROM recipes WHERE is_favorite = 1")
    int getFavoritesCount();

    @Query("SELECT * FROM recipes WHERE is_favorite = 1")
    List<Recipe> getFavoriteRecipes();

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<Ingredient> ingredients);

    @Insert
    void insertInstructions(List<Instruction> instructions);

    @Query("UPDATE recipes SET is_favorite = :isFavorite WHERE id = :recipeId")
    void updateFavoriteStatus(long recipeId, boolean isFavorite);

    @Transaction
    default long insertRecipeWithDetails(Recipe recipe, List<Ingredient> ingredients, List<Instruction> instructions) {
        long recipeId = insertRecipe(recipe);

        // Update foreign keys
        for (Ingredient ingredient : ingredients) {
            ingredient.recipeId = recipeId;
        }
        for (Instruction instruction : instructions) {
            instruction.recipeId = recipeId;
        }

        insertIngredients(ingredients);
        insertInstructions(instructions);

        return recipeId;
    }
}