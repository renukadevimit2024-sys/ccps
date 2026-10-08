class Bank {
    private final String bankId;
    private final String name;
    private final String branch;
    private final String ifscCode;

    public Bank(String bankId, String name, String branch, String ifscCode) {
        this.bankId = bankId;
        this.name = name;
        this.branch = branch;
        this.ifscCode = ifscCode;
    }

    /** Returns null when the payment is approved, otherwise the decline reason. */
    public String checkAuthorization(CreditCard card, double amount) {
        if (!"ACTIVE".equals(card.getStatus())) return "Card is " + card.getStatus();
        if (amount > card.getAvailableCredit()) return "Insufficient credit limit";
        return null;
    }

    public String getName() { return name; }

    @Override
    public String toString() { return name + ", " + branch + " (" + ifscCode + ")"; }
}
