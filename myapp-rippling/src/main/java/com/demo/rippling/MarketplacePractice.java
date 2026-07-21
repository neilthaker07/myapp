package com.demo.rippling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * PROBLEM: Internal Perks & Equipment Marketplace
 *
 * You are building the backend core for an internal employee marketplace.
 * The company grants each employee an annual allowance of "SnowPoints" to spend
 * on office equipment, software licenses, or company swag.
 *
 * 1. Inventory & Pricing:
 *    The store has a catalog of items. Each item has a point cost and a strictly
 *    limited inventory. (e.g., "Standing Desk" costs 300 points, 50 in stock).
 *
 * 2. Purchasing Rules:
 *    Employees can attempt to purchase items one at a time.
 *    A purchase is SUCCESSFUL if and only if:
 *      a) The item exists and has at least 1 unit in stock.
 *      b) The employee has a sufficient point balance to afford the item.
 *    If successful, the system must deduct the inventory AND deduct the points.
 *    If it fails, it returns OUT_OF_STOCK or INSUFFICIENT_FUNDS.
 *
 * 3. Reporting:
 *    You need a method to print an employee's current state: their remaining
 *    point balance and a list of all successfully purchased items.
 *
 * Example Flow:
 *   - Alice is granted 500 SnowPoints.
 *   - "Patagonia Jacket" costs 200 points, Qty: 2.
 *   - Alice buys "Patagonia Jacket" -> SUCCESS. (Alice Balance: 300, Qty: 1)
 *   - Bob (500 pts) buys "Patagonia Jacket" -> SUCCESS. (Bob Balance: 300, Qty: 0)
 *   - Charlie (500 pts) buys "Patagonia Jacket" -> OUT_OF_STOCK.
 *
 * Edge cases to think about:
 *   - What happens if an employee tries to buy an item that doesn't exist in the catalog?
 *   - What happens if someone tries to grant negative points to an employee?
 *
 * Constraints / things to reason about out loud (interview framing):
 *   - The "Flash Sale" Concurrency Problem: Imagine Snowflake drops 100 limited-edition
 *     backpacks, and 4,000 employees click "Buy" at the exact same millisecond.
 *     How do you structure your locks or synchronization to guarantee you don't sell
 *     101 backpacks, while preventing total gridlock (deadlocks) in the system?
 *   - OOD: How do you enforce the transactional boundary? If the inventory deducts successfully,
 *     but the user balance deduction fails, how do you prevent the inventory from being permanently lost?
 */
public class MarketplacePractice {

    public enum PurchaseStatus {
        SUCCESS,
        OUT_OF_STOCK,
        INSUFFICIENT_FUNDS,
        ITEM_NOT_FOUND
    }

    public static void main(String[] args) {
        MarketplaceEngine engine = new MarketplaceEngine();

        // 1. Initialize Store Inventory (Item ID, Name, Price, Quantity)
        engine.addInventory("SKU-001", "Standing Desk", 300, 2);
        engine.addInventory("SKU-002", "Noise Cancelling Headphones", 250, 10);

        // 2. Grant Annual Points to Employees
        engine.grantPoints("EMP-ALICE", 500);
        engine.grantPoints("EMP-BOB", 500);
        engine.grantPoints("EMP-CHARLIE", 500);

        // 3. Simulate Purchases
        System.out.println(engine.purchaseItem("EMP-ALICE", "SKU-001"));   // SUCCESS
        System.out.println(engine.purchaseItem("EMP-BOB", "SKU-001"));     // SUCCESS

        // This should fail because the desk is out of stock
        System.out.println(engine.purchaseItem("EMP-CHARLIE", "SKU-001")); // OUT_OF_STOCK

        // This should fail because Alice only has 200 points left
        System.out.println(engine.purchaseItem("EMP-ALICE", "SKU-002"));   // INSUFFICIENT_FUNDS

        System.out.println("\n--- Alice's Account Statement ---");
        // TODO: Print Alice's remaining balance and purchased items
         engine.printEmployeeStatement("EMP-ALICE");
    }

    @Getter
    @AllArgsConstructor
    static class Item {
        private String itemId;
        private String name;
        private Integer price;
        private Integer qty;

        public void decreaseQty() {
            this.qty--;
        }
    }

    @Getter
    static class EmployeeAccount {
        private final String employeeId;
        private Integer points;
        private final List<Item> purchasedItems;

        public EmployeeAccount(String employeeId) {
            this.employeeId = employeeId;
            this.points = 0;
            this.purchasedItems = new ArrayList<>();
        }

        // Thread-safe atomic update
        public synchronized void addPoints(int amount) {
            this.points += amount;
        }

        public void deductPointsAndRecord(int amount, Item item) {
            this.points -= amount;
            this.purchasedItems.add(item);
        }
    }

    @Getter
    static class EmployeePointsMgmt {
        Map<String, EmployeeAccount> employees = new ConcurrentHashMap<>();
        public void grantPoints(String employeeId, int points) {
//            if (employees.containsKey(employeeId)) {
//                EmployeeAccount employeeAccount = employees.get(employeeId);
//                employeeAccount.setPoints(employeeAccount.getPoints() + points);
//            } else {
//                EmployeeAccount employeeAccount = new EmployeeAccount(employeeId, points, new ArrayList<>());
//                employees.put(employeeId, employeeAccount);
//            }
            EmployeeAccount account = employees.computeIfAbsent(
                    employeeId,
                    k -> new EmployeeAccount(employeeId)
            );
            account.addPoints(points);
        }

        public EmployeeAccount getAccount(String employeeId) {
            return employees.get(employeeId);
        }

//        public void updatePoints(String employeeId, int points) {
//            EmployeeAccount employeeAccount = employees.get(employeeId);
//            employeeAccount.setPoints(employeeAccount.getPoints() - points);
//        }
//
//        public void updateRecords(String employeeId, Item item) {
//            EmployeeAccount employeeAccount = employees.get(employeeId);
//            List<Item> items = employeeAccount.getPurchasedItems();
//            items.add(item);
//            employeeAccount.setPurchasedItems(items);
//        }
    }

    @Getter
    static class InventoryMgmt {
        Map<String, Item> inventory = new ConcurrentHashMap<>();
        void addInventory(String sku, String name, int price, int quantity) {
            inventory.computeIfAbsent(sku, k -> new Item(sku, name, price, quantity));
        }

//        void updateInventory(String sku) {
//            Item item = inventory.get(sku);
//            item.setQty(item.getQty() - 1);
//        }

        public Item getItem(String sku) {
            return inventory.get(sku);
        }
    }

    static class MarketplaceEngine {
        InventoryMgmt inventoryMgmt;
        EmployeePointsMgmt employeePointsMgmt;
        public MarketplaceEngine() {
            // Initialize your collections here
            inventoryMgmt = new InventoryMgmt();
            employeePointsMgmt = new EmployeePointsMgmt();
        }

        public void addInventory(String sku, String name, int price, int quantity) {
            inventoryMgmt.addInventory(sku,name,price,quantity);
        }

        public void grantPoints(String employeeId, int points) {
            employeePointsMgmt.grantPoints(employeeId, points);
        }

        public PurchaseStatus purchaseItem(String employeeId, String sku) throws IllegalArgumentException {
            // 1. Fetch data exactly once
            Item item = inventoryMgmt.getItem(sku);
            if (item == null) {
                return PurchaseStatus.ITEM_NOT_FOUND;
            }

            EmployeeAccount employee = employeePointsMgmt.getAccount(employeeId);
            if (employee == null) {
                throw new IllegalArgumentException("Invalid employee ID: " + employeeId);
            }

            // 2. Granular Locking: Lock the employee account first
            synchronized (employee) {
                if (employee.getPoints() < item.getPrice()) {
                    return PurchaseStatus.INSUFFICIENT_FUNDS;
                }

                // 3. Lock the item second (ensures no deadlocks and no global freeze)
                synchronized (item) {
                    if (item.getQty() <= 0) {
                        return PurchaseStatus.OUT_OF_STOCK;
                    }

                    // 4. Execute the atomic transaction boundary
                    item.decreaseQty();
                    employee.deductPointsAndRecord(item.getPrice(), item);
                    return PurchaseStatus.SUCCESS;
                }
            }

            // item exist
//            Map<String, Item> inventory = inventoryMgmt.getInventory();
//            Map<String, EmployeeAccount> employees = employeePointsMgmt.getEmployees();
//            if (!inventory.containsKey(sku)) {
//                return PurchaseStatus.ITEM_NOT_FOUND;
//            }
//            // inventory count > 0
//            if (inventory.get(sku).getQty() == 0) {
//                return PurchaseStatus.OUT_OF_STOCK;
//            }
//            if (!employees.containsKey(employeeId)) {
//                throw new Exception("invalid employee");
//            }
//            // employeeId points available for item purchase
//            if (employees.get(employeeId).getPoints() < inventory.get(sku).getPrice()) {
//                return PurchaseStatus.INSUFFICIENT_FUNDS;
//            }
//            // update inventory
//            inventoryMgmt.updateInventory(sku);
//            // update points
//            employeePointsMgmt.updatePoints(employeeId, inventory.get(sku).getPrice());
//            // update employee records
//            employeePointsMgmt.updateRecords(employeeId, inventory.get(sku));
        }

        public void printEmployeeStatement(String employeeId) {
//            Map<String, EmployeeAccount> employees = employeePointsMgmt.getEmployees();
//            EmployeeAccount employeeAccount = employees.get(employeeId);
//            System.out.println(employeeId + " " + employeeAccount.getPoints());
//            for (Item item : employeeAccount.getPurchasedItems()) {
//                System.out.println(item.getItemId());
//            }
            EmployeeAccount employee = employeePointsMgmt.getAccount(employeeId);
            if (employee != null) {
                System.out.println(employeeId + " Balance: " + employee.getPoints());
                for (Item item : employee.getPurchasedItems()) {
                    System.out.println("- " + item.getItemId() + " (" + item.getName() + ")");
                }
            }
        }
    }

    // Feel free to create helper classes like EmployeeWallet, InventoryItem, etc.
}



// Hint: You need a way to store the Catalog and the Employee accounts.
// What data structures give you fast O(1) lookups and handle concurrency well?