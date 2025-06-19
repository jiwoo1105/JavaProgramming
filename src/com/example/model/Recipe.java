package com.example.model;

import java.util.List;
import java.util.ArrayList;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private List<Ingredient> ingredients;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(int id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>();
    }

    // 비즈니스 로직을 포함하는 메서드들
    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("재료는 null일 수 없습니다.");
        }
        if (ingredients.contains(ingredient)) {
            throw new IllegalStateException("이미 존재하는 재료입니다.");
        }
        ingredients.add(ingredient);
    }

    public void validateRecipe() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("레시피 이름은 필수입니다.");
        }
        if (instructions == null || instructions.trim().isEmpty()) {
            throw new IllegalStateException("조리 방법은 필수입니다.");
        }
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("최소 하나의 재료가 필요합니다.");
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    @Override
    public String toString() {
        return name;
    }
} 