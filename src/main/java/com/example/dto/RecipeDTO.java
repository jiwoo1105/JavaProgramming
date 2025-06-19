package com.example.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeDTO {
    private int id;
    private String name;
    private String instructions;
    private List<Integer> ingredientIds;

    public RecipeDTO() {
        this.ingredientIds = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public List<Integer> getIngredientIds() { return ingredientIds; }
    public void setIngredientIds(List<Integer> ingredientIds) { this.ingredientIds = ingredientIds; }
} 