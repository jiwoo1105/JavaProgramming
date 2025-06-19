package com.example.model;

public class Ingredient {
    private int id;
    private String name;
    private int availableQuantity;

    public Ingredient() {}

    public Ingredient(String name, int availableQuantity) {
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    // 재료 사용 가능 여부 확인
    public boolean hasEnoughQuantity(int requiredQuantity) {
        return availableQuantity >= requiredQuantity;
    }

    // 재료 사용
    public void useQuantity(int quantity) {
        if (availableQuantity < quantity) {
            throw new IllegalStateException("사용 가능한 재료 수량이 부족합니다.");
        }
        this.availableQuantity -= quantity;
    }

    // 재료 추가
    public void addQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("추가할 수량은 0보다 커야 합니다.");
        }
        this.availableQuantity += quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return name + ", " + availableQuantity;
    }
} 