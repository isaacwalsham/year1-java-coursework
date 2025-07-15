package arcade;

import java.io.*;
import java.util.*;

public class Simulation {

    private static List<Customer> customers = new ArrayList<>();
    private static Map<String, ArcadeGame> games = new HashMap<>();

    public static void main(String[] args) throws IOException, InvalidGameIdException {
        loadCustomers("src/customers.txt");
        loadGames("src/games.txt");
        processTransactions("src/transactions.txt");
    }

    public static void loadCustomers(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] data = line.split(",");
            if (data.length < 5) continue;
            String id = data[0].trim();
            String name = data[1].trim();
            int age = Integer.parseInt(data[2].trim());
            int balance = Integer.parseInt(data[3].trim());
            DiscountType discountType = DiscountType.valueOf(data[4].trim().toUpperCase());
            Customer customer = new Customer(id, name, age, discountType);
            customer.addFunds(balance);
            customers.add(customer);
        }
        br.close();
    }

    public static void loadGames(String filename) throws IOException, InvalidGameIdException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] data = line.split("@");
            if (data.length < 5) {
                System.out.println("Skipping invalid game line: " + line);
                continue;
            }

            String id = data[0].trim();
            String title = data[1].trim().replace("\"", "");
            String type = data[2].trim().toLowerCase();
            int price = Integer.parseInt(data[3].trim());

            ArcadeGame game = null;

            switch (type) {
                case "cabinet":
                    boolean payoutRewards = data[4].trim().equalsIgnoreCase("yes");
                    game = new CabinetGame(id, title, price, 0, payoutRewards);
                    break;

                case "active":
                    int minAgeActive = Integer.parseInt(data[4].trim());
                    game = new ActiveGame(id, title, price, minAgeActive);
                    break;

                case "virtualreality":
                    if (data.length < 6) {
                        System.out.println("Skipping incomplete virtual game line: " + line);
                        continue;
                    }
                    int minAgeVR = Integer.parseInt(data[4].trim());
                    EquipmentType equipment = EquipmentType.valueOf(data[5].trim().toUpperCase());
                    game = new VirtualRealityGame(id, title, price, minAgeVR, equipment);
                    break;

                default:
                    System.out.println("Unknown game type: " + type + " in line: " + line);
            }

            if (game != null) {
                games.put(id, game);
            }
        }

        br.close();
    }

    public static void processTransactions(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] data = line.split(",");
            String command = data[0].trim().toUpperCase();

            switch (command) {
                case "PLAY":
                    handlePlay(data);
                    break;
                case "ADD_FUNDS":
                    handleAddFunds(data);
                    break;
                case "NEW_CUSTOMER":
                    handleNewCustomer(data);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        }
        br.close();
    }

    private static void handlePlay(String[] data) {
        if (data.length < 4) return;
        String customerId = data[1].trim();
        String gameId = data[2].trim();
        boolean peak = data[3].trim().equalsIgnoreCase("PEAK");

        Customer customer = findCustomerById(customerId);
        ArcadeGame game = games.get(gameId);

        if (customer == null) {
            customer = new Customer(customerId, "Customer " + customerId, 18, DiscountType.NONE);
            customer.addFunds(1000);
            customers.add(customer);
            System.out.println("Auto-created new customer: " + customer.getCustomerId());
        }

        if (game == null) {
            System.out.println("❌ Game not found: " + gameId);
            return;
        }

        simulateGame(customer, game, peak);
    }

    private static void handleAddFunds(String[] data) {
        if (data.length < 3) return;
        String customerId = data[1].trim();
        int amount = Integer.parseInt(data[2].trim());

        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            customer.addFunds(amount);
            System.out.printf("💰 Added £%.2f to %s’s account. New balance: £%.2f%n",
                    amount / 100.0, customer.getName(), customer.getBalance() / 100.0);
        }
    }

    private static void handleNewCustomer(String[] data) {
        if (data.length < 6) return;
        String id = data[1].trim();
        String name = data[2].trim();
        String discountRaw = data[3].trim().toUpperCase();
        if (discountRaw.equals("STAFF")) discountRaw = "CMP_STAFF";
        DiscountType discountType = DiscountType.valueOf(discountRaw);
        int balance = Integer.parseInt(data[4].trim());
        int age = Integer.parseInt(data[5].trim());

        Customer customer = new Customer(id, name, age, discountType);
        customer.addFunds(balance);
        customers.add(customer);
        System.out.println("👤 New customer added: " + name + " (" + id + ")");
    }

    private static Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public static void simulateGame(Customer customer, ArcadeGame game, boolean peak) {
        String customerLabel = customer.getName().startsWith("Customer ") ?
                "Customer " + customer.getCustomerId() : customer.getName();

        System.out.println("--------------------------------------------------");
        System.out.println(customerLabel + " is attempting to play: " + game.getGameTitle());
        System.out.println("⏰ Peak time: " + (peak ? "Yes" : "No"));
        System.out.printf("🎮 Price to play: £%.2f%n", game.calculatePrice(peak) / 100.0);

        try {
            customer.chargeAccount(game, peak);
            System.out.printf("✅ SUCCESS: %s played %s%n", customerLabel, game.getGameTitle());
            System.out.printf("💷 Remaining balance: £%.2f%n", customer.getBalance() / 100.0);
        } catch (InsufficientBalanceException e) {
            System.out.printf("❌ FAIL: Not enough funds to play %s%n", game.getGameTitle());
        } catch (AgeLimitException e) {
            System.out.printf("❌ FAIL: Age restriction — %s is too young to play %s%n", customerLabel, game.getGameTitle());
        }

        System.out.println("--------------------------------------------------\n");
    }
}