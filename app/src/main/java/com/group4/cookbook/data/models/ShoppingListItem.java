package com.group4.cookbook.data.models;

import java.util.List;

public class ShoppingListItem {
    private String category;
    private String name;
    private boolean purchased;
    private List<String> recipes; // recipe IDs that contributed this item
    private double totalQuantity;
    private String unit;

    public ShoppingListItem() {}

    public ShoppingListItem(String name, String category, double quantity, String unit) {
        this.name = name;
        this.category = category;
        this.totalQuantity = quantity;
        this.unit = unit;
        this.purchased = false;
    }

    // Getters and setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }

    public List<String> getRecipes() { return recipes; }
    public void setRecipes(List<String> recipes) { this.recipes = recipes; }

    public double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(double totalQuantity) { this.totalQuantity = totalQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}