import java.util.*;

class Customer {
    private final String customerId;
    private final String name;
    private final String email;
    private final String phone;
    private final String address;
    private final List<CreditCard> cards = new ArrayList<>();

    public Customer(String customerId, String name, String email, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public void createAccount() {
        System.out.println("Account created for customer: " + name + " (" + customerId + ")");
    }

    public Transaction makePayment(CreditCard card, Merchant merchant, double amount, PaymentService service) {
        return service.process(card, merchant, amount);
    }

    public void viewStatement(List<Transaction> all) {
        System.out.println("Statement for " + name + ":");
        boolean any = false;
        for (Transaction t : all) {
            if (t.getCard().getOwner() == this) {
                System.out.println("  " + t);
                any = true;
            }
        }
        if (!any) System.out.println("  (no transactions)");
    }

    public void addCard(CreditCard card) { cards.add(card); }
    public List<CreditCard> getCards() { return cards; }
    public String getName() { return name; }
    public String getCustomerId() { return customerId; }
    public String getEmail() { return email; }
}
