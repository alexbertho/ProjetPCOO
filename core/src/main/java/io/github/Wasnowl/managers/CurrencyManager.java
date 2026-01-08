package io.github.Wasnowl.managers;

/**
 * Simple currency manager for in-game money.
 */
public class CurrencyManager {
    private int balance;

    public CurrencyManager(int startingBalance) {
        this.balance = startingBalance;
    }

    public int getBalance() {
        return balance;
    }

    public void add(int amount) {
        if (amount <= 0) return;
        balance += amount;
    }

    public boolean canAfford(int amount) {
        return balance >= amount;
    }

    public boolean spend(int amount) {
        if (!canAfford(amount)) return false;
        balance -= amount;
        return true;
    }
}
