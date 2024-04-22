/*
    Title: Inventory Project
    Author: Osy Okocha
    Date: March 13, 2024

*/

import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Initialize locations
        Location locker = new Location("Locker");
        Location backpack = new Location("Backpack");
        Location pencilCase = new Location("Pencil Case");

        // List of all files to load
        String[] files = {"Locker.txt", "Locker.csv", "Backpack.txt", "Backpack.csv", "Pencil_Case.txt", "Pencil_Case.csv"};

        // Load inventory from files
        boolean fileExists = false;
        for (String fileName : files) {
            try {
                Location location = getLocationByName(fileName.split("\\.")[0].replace("_", " "), locker, backpack, pencilCase);
                if (location != null && loadInventoryFromFile(location, fileName)) {
                    fileExists = true;
                }
            } catch (IOException e) {
                System.out.println("An error occurred while loading the inventory.");
            }
        }

        if (!fileExists) { // populates with dummy data if no previous data is found
            System.out.println("There is no previous data to load.");
            // Initialize inventory items
            Inventory pencil = new Inventory("Pencil");
            Inventory eraser = new Inventory("Eraser");
            Inventory ipad = new Inventory("iPad");
            Inventory laptop = new Inventory("Laptop");
            Inventory jacket = new Inventory("Jacket");
            Inventory vaseline = new Inventory("Vaseline");

            // Add items to locations
            pencilCase.addInventory(pencil);
            pencilCase.addInventory(eraser);
            backpack.addInventory(ipad);
            backpack.addInventory(laptop);
            locker.addInventory(jacket);
            locker.addInventory(vaseline);
        }


        Scanner sc = new Scanner(System.in);
        programCommands:
        while (true) {
            try { // try block to catch any exceptions that occur during user input
                System.out.println("Enter command (add/remove/move/view/export/quit):");
                String command = sc.nextLine();

                // Process user commands
                switch (command) {
                    case "quit":
                        break programCommands;
                    case "add":
                        while (true) {
                            Location location = selectLocation(sc, "Enter location (locker/backpack/pencil case):", locker, backpack, pencilCase);
                            if (location == null) continue;

                            System.out.println("Enter item name:");
                            String itemName = sc.nextLine();

                            Inventory newItem = new Inventory(itemName);
                            location.addInventory(newItem);
                            System.out.println(itemName + " added to " + location.getName());

                            System.out.println("Do you want to add more items? (yes/no):");
                            String response = sc.nextLine();

                            if (response.equalsIgnoreCase("no")) {
                                break;
                            }
                        }
                        break;
                    case "remove":
                        while (true) {
                            Location location = selectLocation(sc, "Enter location (locker/backpack/pencil case):", locker, backpack, pencilCase);
                            if (location == null) continue;

                            System.out.println("Enter item name:");
                            String itemName = sc.nextLine();

                            Inventory item = getItemByName(itemName, location.getInventory());
                            if (item == null) {
                                System.out.println("Invalid item. Please try again.");
                                continue;
                            }

                            location.removeInventory(item);
                            System.out.println(itemName + " removed from " + location.getName());

                            System.out.println("Do you want to remove more items? (yes/no):");
                            String response = sc.nextLine();

                            if (response.equalsIgnoreCase("no")) {
                                break;
                            }
                        }
                        break;
                    case "move":
                        // Move an item from one location to another
                        Location sourceLocation = selectLocation(sc, "Enter source location (locker/backpack/pencil case):", locker, backpack, pencilCase);
                        if (sourceLocation == null) continue; // validity checker


                        Location destinationLocation = selectLocation(sc, "Enter destination location (locker/backpack/pencil case):", locker, backpack, pencilCase);
                        if (destinationLocation == null) continue; // validity checker


                        // Print the items in the source location
                        System.out.println("Items in " + sourceLocation.getName() + ":");
                        for (Inventory item : sourceLocation.getInventory()) {
                            System.out.println(item.getName());
                        }
                        System.out.println(); // print a blank line to separate the items from the "Enter item name" prompt


                        System.out.println("Enter item name:");
                        String itemName = sc.nextLine();

                        Inventory item = getItemByName(itemName, sourceLocation.getInventory());
                        if (item == null) {
                            System.out.println("Invalid item. Please try again.");
                            continue;
                        }

                        sourceLocation.moveInventory(item, destinationLocation); // move the item from the source location to the destination location

                        break;
                    case "view":
                        // View items in a location
                        Location location = selectLocation(sc, "Enter location (locker/backpack/pencil case):", locker, backpack, pencilCase);
                        if (location == null) continue; // validity checker


                        System.out.println("Items in " + location.getName() + ":");
                        for (Inventory viewItem : location.getInventory()) {
                            System.out.println(viewItem.getName());
                        } // print the items in the specified location
                        System.out.println(); // print a blank line to separate the items from the "Enter command" prompt

                        break;
                    case "export":
                        System.out.println("Enter location (locker/backpack/pencil case/all):");
                        String exportlocationName = sc.nextLine();

                        System.out.println("Enter file type (.txt/.csv):");
                        String fileType = sc.nextLine();

                        if (exportlocationName.equals("all")) {
                            exportAllLocations(fileType, locker, backpack, pencilCase);
                        } else {
                            Location exportLocation = getLocationByName(exportlocationName, locker, backpack, pencilCase);
                            if (exportLocation == null) {
                                System.out.println("Invalid location. Please try again.");
                                continue;
                            }
                            exportLocation(exportLocation, fileType);
                        }
                        break;
                    default:
                        System.out.println("Invalid command. Please try again.");
                        break;
                }
            } catch (Exception e) { // catch any exceptions that occur during user input
                System.out.println("An error occurred. Please try again.");
            }
        }

        sc.close(); // close the scanner when the program is done running to reduce resource usage
    }

    /**
     * resource: javadoc comment tags - "https://ioflood.com/blog/javadoc-comments/#:~:text=Javadoc%20comments%20in%20Java%20are,and%20to%20your%20future%20self"
     * <p>
     * Prompts the user to select a location and returns the selected location.
     *
     * @param sc        The scanner to read user input - I use sc because its simpler (I am extremely lazy)
     * @param prompt    The prompt to display to the user - Easier to understand from a code review perspective
     * @param locations The possible locations to select from e.g. locker, backpack, pencil case
     * @return The selected location, or null if the location is invalid - initially i found an error
     * where the program would crash if the location was written wrong, so I also had to add a validity
     * checker in the get location by name function to cover lowercase and spaces in the pencilCase location.
     * the
     */
    private static Location selectLocation(Scanner sc, String prompt, Location... locations) {
        System.out.println(prompt);
        String locationName = sc.nextLine();
        Location location = getLocationByName(locationName, locations);
        if (location == null) {
            System.out.println("Invalid location. Please try again.");
        }
        return location;
    }

    /**
     * Returns the location with the given name, or null if the location doesn't exist.
     *
     * @param name      The name of the location to find.
     * @param locations The possible locations to select from.
     * @return The location with the given name, or null if the location exists.
     */
    private static Location getLocationByName(String name, Location... locations) {
        String formattedName = name.replace(" ", "");
        for (Location location : locations) {
            if (location.getName().replace(" ", "").equalsIgnoreCase(formattedName)) {
                return location;
            }
        }
        return null;
    }

    /**
     * Returns the inventory item with the given name from the given inventory list, or null if the item does not exist.
     *
     * @param name      The name of the item to find - for the move and remove commands, so the program doesn't crash if you select to run it on an item that does not exist
     * @param inventory The list of inventory items to search in - a for each loop to search through the inventory list
     * @return The inventory item with the given name, or null if no  item exists.
     */
    private static Inventory getItemByName(String name, ArrayList<Inventory> inventory) {
        for (Inventory item : inventory) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    private static void exportLocation(Location location, String fileType) throws IOException {
        String fileName = location.getName().replace(" ", "_") + fileType;
        try (PrintWriter writer = new PrintWriter((fileName))) {
            writer.println("The following items are in " + location.getName() + ":\n");
            for (Inventory item : location.getInventory()) {
                writer.println("· " + item.getName()); // replicating a bulleted list to make the exported file cleaner - I'll take any opportunity to use an interpunct
            }
        }
        System.out.println("Exported " + location.getName() + " to " + fileName);
    }

    private static void exportAllLocations(String fileType, Location... locations) throws IOException { // resource: https://www.geeksforgeeks.org/variable-arguments-varargs-in-java/ - "..." is a "varargs" parameter that allows the method to accept a variable number of arguments
        for (Location location : locations) {
            exportLocation(location, fileType);
        }
    }

    private static boolean loadInventoryFromFile(Location location, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        try (Scanner scanner = new Scanner(file)) {
            // Skip the first two lines if it's a text file
            if (fileName.endsWith(".txt")) {
                if (scanner.hasNextLine()) scanner.nextLine();
                if (scanner.hasNextLine()) scanner.nextLine();
            }

            // Read items and add them to the location's inventory
            while (scanner.hasNextLine()) {
                String itemName = scanner.nextLine();
                if (fileName.endsWith(".txt")) {
                    itemName = itemName.substring(2); // Remove the "· " prefix for .txt files
                } else if (fileName.endsWith(".csv")) {
                    itemName = itemName.split(",")[0]; // Gets the first column for .csv files
                }

                Inventory item = new Inventory(itemName);
                location.addInventory(item);
            }
        }

        System.out.println("Loaded inventory from " + fileName + " to " + location.getName());
        return true;
    }
}

