/*

    Title: Inventory Project
    Author: Osy Okocha
    Date: March 13, 2024
    
*/

import java.util.ArrayList;

public class Location {
    private final String name; // final use similar to Inventory.java
    private final ArrayList<Inventory> inventory;

    public Location(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }


    public ArrayList<Inventory> getInventory() {
        return inventory;
    }


    public void addInventory(Inventory item) {
        inventory.add(item);
    }

    public void removeInventory(Inventory item) {
        inventory.remove(item);
    }

    public void moveInventory(Inventory item, Location destination) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            destination.addInventory(item);
        }
    }
}
