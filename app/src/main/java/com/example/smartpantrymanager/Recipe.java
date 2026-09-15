package com.example.smartpantrymanager;

import java.util.List;

public class Recipe {
    private int id;
    private String title;
    private String category;
    private String instructions;
    private List<String> requiredIngredients;
    private double matchPercentage;
    private List<String> missingIngredients;

    public Recipe(int id, String title, String category, String instructions, List<String> requiredIngredients) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.instructions = instructions;
        this.requiredIngredients = requiredIngredients;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getInstructions() { return instructions; }
    public List<String> getRequiredIngredients() { return requiredIngredients; }

    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) { this.matchPercentage = matchPercentage; }

    public List<String> getMissingIngredients() { return missingIngredients; }
    public void setMissingIngredients(List<String> missingIngredients) { this.missingIngredients = missingIngredients; }
}