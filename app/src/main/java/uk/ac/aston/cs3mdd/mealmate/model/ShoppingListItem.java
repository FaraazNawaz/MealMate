package uk.ac.aston.cs3mdd.mealmate.model;


public class ShoppingListItem {
    private String itemName;
    private boolean completed;

    public ShoppingListItem(String itemName, boolean completed) {
        this.itemName = itemName;
        this.completed = completed;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
