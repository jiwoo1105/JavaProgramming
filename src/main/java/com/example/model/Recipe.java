package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private Map<Ingredient, Integer> requiredIngredients; // 재료와 필요한 수량
    private boolean isFavorite;
    private int rating;
    private String note;
    private java.sql.Timestamp lastCookedAt;
    private Map<String, Integer> requiredIngredientNames = new HashMap<>();

    public Recipe() {
        this.requiredIngredients = new HashMap<>();
        this.isFavorite = false;
    }

    public Recipe(String name, String instructions) {
        this();
        this.name = name;
        this.instructions = instructions;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public Map<Ingredient, Integer> getRequiredIngredients() { return requiredIngredients; }
    public void setRequiredIngredients(Map<Ingredient, Integer> requiredIngredients) { 
        this.requiredIngredients = requiredIngredients; 
    }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public int getRating() { return rating; }
    public void setRating(int rating) { 
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1에서 5 사이여야 합니다.");
        }
        this.rating = rating; 
    }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public java.sql.Timestamp getLastCookedAt() { return lastCookedAt; }
    public void setLastCookedAt(java.sql.Timestamp lastCookedAt) { this.lastCookedAt = lastCookedAt; }
    public Map<String, Integer> getRequiredIngredientNames() {
        return requiredIngredientNames;
    }

    // 비즈니스 로직
    public void addIngredient(Ingredient ingredient, int requiredQuantity) {
        if (ingredient == null) {
            throw new IllegalArgumentException("재료는 null일 수 없습니다.");
        }
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("필요한 수량은 0보다 커야 합니다.");
        }
        requiredIngredients.put(ingredient, requiredQuantity);
    }

    public void removeIngredient(Ingredient ingredient) {
        requiredIngredients.remove(ingredient);
    }

    // 요리 가능 여부 확인
    public boolean canCook() {
        for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
            if (!entry.getKey().hasEnoughQuantity(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    // 부족한 재료 목록 반환
    public List<String> getMissingIngredients() {
        List<String> missingIngredients = new ArrayList<>();
        for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int required = entry.getValue();
            if (!ingredient.hasEnoughQuantity(required)) {
                int missing = required - ingredient.getAvailableQuantity();
                missingIngredients.add(String.format("%s (%d개 부족)", 
                    ingredient.getName(), missing));
            }
        }
        return missingIngredients;
    }

    // 요리하기 (재료 사용)
    public void cook() {
        if (!canCook()) {
            List<String> missing = getMissingIngredients();
            throw new IllegalStateException(
                "다음 재료가 부족합니다:\n" + String.join("\n", missing));
        }

        for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
            entry.getKey().useQuantity(entry.getValue());
        }
        this.lastCookedAt = new java.sql.Timestamp(System.currentTimeMillis());
    }

    public void addIngredientNameAndQuantity(String name, int quantity) {
        requiredIngredientNames.put(name, quantity);
    }

    public void cookedNow() {
        this.lastCookedAt = new java.sql.Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return String.format("%s (★%d) %s", 
            name, 
            rating,
            isFavorite ? "[즐겨찾기]" : "");
    }
} 