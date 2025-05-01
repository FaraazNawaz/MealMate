package uk.ac.aston.cs3mdd.mealmate.model;



public class Category {
    private String name;
    private String image;

    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
