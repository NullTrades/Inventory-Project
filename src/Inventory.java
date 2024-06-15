/*

    Title: Inventory Project
    Author: Osy Okocha
    Date: March 13, 2024

*/

public class Inventory {
    private final String name; // final keyword added to make the name field immutable - cannot be changed after initialization - mainly used to prevent my IDE from catching a bug and to maintain proper structure

    public Inventory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}