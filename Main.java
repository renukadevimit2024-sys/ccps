import java.util.*;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final List<Customer> customers = new ArrayList<>();
    private static final Bank bank = new Bank("B01", "Demo Bank", "Chennai Main", "DEMO0001234");
    private static final Merchant merchant = new Merchant("M001", "Aurora Coffee Co.", "Food & Beverage");
    private static final PaymentService service = new PaymentService(new PaymentGateway("GW01", "DemoPay"));
    private static int cardSeq = 0;
    private static int billSeq = 0;

    public static void main(String[] args) {
        System.out.println("=== Credit Card Processing System ===");
        ask("Username");
        ask("Password");
        System.out.println("Login successful (demo).");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Register customer");
            System.out.println("2. Add credit card");
            System.out.println("3. Validate card");
            System.out.println("4. Make payment");
            System.out.println("5. Transaction history");
            System.out.println("6. Customer statement");
            System.out.println("7. Generate bill");
            System.out.println("8. Reports");
            System.out.println("0. Exit");
            switch (ask("Choose")) {
                case "1": registerCustomer(); break;
                case "2": addCard(); break;
                case "3": validateCard(); break;
                case "4": makePayment(); break;
                case "5": history(); break;
                case "6": statement(); break;
                case "7": bill(); break;
                case "8": ReportService.printSummary(service.getTransactions()); break;
                case "0": running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static String ask(String label) {
        System.out.print(label + ": ");
        return in.nextLine().trim();
    }

    private static Customer pickCustomer() {
        if (customers.isEmpty()) {
            System.out.println("No customers yet. Register one first.");
            return null;
        }
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        try {
            return customers.get(Integer.parseInt(ask("Select customer number")) - 1);
        } catch (RuntimeException e) {
            System.out.println("Invalid selection.");
            return null;
        }
    }

    private static CreditCard pickCard(Customer c) {
        List<CreditCard> cards = c.getCards();
        if (cards.isEmpty()) {
            System.out.println("This customer has no cards. Add one first.");
            return null;
        }
        for (int i = 0; i < cards.size(); i++) {
            CreditCard cc = cards.get(i);
            System.out.printf("%d. %s %s (available $%.2f)%n", i + 1, cc.getCardType(), cc.getMasked(), cc.getAvailableCredit());
        }
        try {
            return cards.get(Integer.parseInt(ask("Select card number")) - 1);
        } catch (RuntimeException e) {
            System.out.println("Invalid selection.");
            return null;
        }
    }

    private static void registerCustomer() {
        String name = ask("Full name");
        String email = ask("Email");
        String phone = ask("Phone");
        String address = ask("Address");
        Customer c = new Customer("C" + (customers.size() + 1), name, email, phone, address);
        c.createAccount();
        customers.add(c);
    }

    private static void addCard() {
        Customer c = pickCustomer();
        if (c == null) return;
        String number = ask("Card number");
        String expiry = ask("Expiry (MM/YY)");
        String cvv = ask("CVV");
        String type = ask("Card type (VISA/MASTERCARD)");
        try {
            double limit = Double.parseDouble(ask("Credit limit"));
            CreditCard card = new CreditCard("CC" + (++cardSeq), number, expiry, cvv, type, limit, c, bank);
            System.out.println("Card added: " + card.getMasked() + " issued by " + bank);
        } catch (NumberFormatException e) {
            System.out.println("Invalid credit limit. Card not added.");
        }
    }

    private static void validateCard() {
        Customer c = pickCustomer();
        if (c == null) return;
        CreditCard card = pickCard(c);
        if (card == null) return;
        String error = card.validationError();
        System.out.println(error == null ? "Card is VALID and active." : "Invalid Card: " + error);
    }

    private static void makePayment() {
        Customer c = pickCustomer();
        if (c == null) return;
        CreditCard card = pickCard(c);
        if (card == null) return;
        double amount;
        try {
            amount = Double.parseDouble(ask("Amount"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }
        Transaction t = c.makePayment(card, merchant, amount, service);
        switch (t.getStatus()) {
            case "SUCCESS":
                System.out.println(ReceiptGenerator.generate(t));
                if (t.isFlagged()) System.out.println("NOTE: transaction flagged as suspicious for manual review.");
                break;
            case "DECLINED":
                System.out.println("Payment Declined: " + t.getReason());
                break;
            default:
                System.out.println("Payment Failed: " + t.getReason());
        }
    }

    private static void history() {
        if (service.getTransactions().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : service.getTransactions()) System.out.println(t);
    }

    private static void statement() {
        Customer c = pickCustomer();
        if (c != null) c.viewStatement(service.getTransactions());
    }

    private static void bill() {
        Customer c = pickCustomer();
        if (c != null) System.out.println(ReportService.generateBill(c, service.getTransactions(), ++billSeq));
    }
}
