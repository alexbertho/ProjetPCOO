package io.github.Wasnowl.managers;

/**
 * Simple currency manager for in-game money.
 */
public class CurrencyManager {
    private int balance;

    /**
     * Cree un gestionnaire d'argent avec un solde initial.
     * @param startingBalance solde initial
     */
    public CurrencyManager(int startingBalance) {
        this.balance = startingBalance;
    }

    /**
     * Retourne le solde actuel.
     * @return solde
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Ajoute de l'argent au solde.
     * @param amount montant a ajouter
     */
    public void add(int amount) {
        if (amount <= 0) return;
        balance += amount;
    }

    /**
     * Indique si le solde permet un achat.
     * @param amount cout a verifier
     * @return true si possible
     */
    public boolean canAfford(int amount) {
        return balance >= amount;
    }

    /**
     * Tente de depenser un montant, retourne true si reussi.
     * @param amount montant a depenser
     * @return true si la depense a reussi
     */
    public boolean spend(int amount) {
        if (!canAfford(amount)) return false;
        balance -= amount;
        return true;
    }
}
