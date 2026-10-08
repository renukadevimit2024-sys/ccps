import java.time.YearMonth;

class CreditCard {
    private final String cardId;
    private final String cardNumber;
    private final String expiryDate;   // MM/YY
    private final String cvv;
    private final String cardType;
    private final double creditLimit;
    private double usedCredit = 0;
    private String status = "ACTIVE";  // ACTIVE, BLOCKED, EXPIRED
    private final Customer owner;
    private final Bank bank;

    public CreditCard(String cardId, String cardNumber, String expiryDate, String cvv,
                      String cardType, double creditLimit, Customer owner, Bank bank) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
        this.creditLimit = creditLimit;
        this.owner = owner;
        this.bank = bank;
        owner.addCard(this);
    }

    public boolean validate() { return validationError() == null; }

    /** Returns null if the card is valid, otherwise the reason it is not. */
    public String validationError() {
        if (!"ACTIVE".equals(status)) return "Card is " + status;
        if (!luhnCheck(cardNumber)) return "Invalid card number";
        if (!isExpiryValid(expiryDate)) return "Card expired or invalid expiry date";
        if (cvv == null || !cvv.matches("\\d{3,4}")) return "Invalid CVV";
        return null;
    }

    private static boolean luhnCheck(String number) {
        String digits = number.replaceAll("\\D", "");
        if (digits.length() < 13 || digits.length() > 19) return false;
        int sum = 0;
        boolean alternate = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    private static boolean isExpiryValid(String exp) {
        try {
            String[] p = exp.split("/");
            if (p.length != 2) return false;
            YearMonth ym = YearMonth.of(2000 + Integer.parseInt(p[1].trim()), Integer.parseInt(p[0].trim()));
            return !ym.isBefore(YearMonth.now());
        } catch (RuntimeException e) {
            return false;
        }
    }

    public double getAvailableCredit() { return creditLimit - usedCredit; }
    public void useCredit(double amount) { usedCredit += amount; }
    public void releaseCredit(double amount) { usedCredit -= amount; }
    public void updateStatus(String newStatus) { this.status = newStatus; }

    public String getCardType() { return cardType; }
    public String getCardId() { return cardId; }
    public String getStatus() { return status; }
    public double getCreditLimit() { return creditLimit; }
    public Customer getOwner() { return owner; }
    public Bank getBank() { return bank; }
    public String getMasked() { return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4); }
}
