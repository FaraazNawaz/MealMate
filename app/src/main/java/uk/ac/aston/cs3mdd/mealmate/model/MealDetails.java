package uk.ac.aston.cs3mdd.mealmate.model;

public class MealDetails {
    private String name;
    private String category;
    private String area;
    private String instructions;
    private String imageUrl;

    public MealDetails(String name, String category, String area, String instructions, String imageUrl) {
        this.name = name;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}