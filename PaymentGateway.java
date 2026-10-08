import java.util.Random;

class PaymentGateway {
    private final String gatewayId;
    private final String provider;
    private String status = "READY";
    private String lastMessage = "";
    private final Random random = new Random();

    public PaymentGateway(String gatewayId, String provider) {
        this.gatewayId = gatewayId;
        this.provider = provider;
    }

    /** Asks the card's bank for approval. Returns an auth code, or null if declined. */
    public String authorize(CreditCard card, double amount) {
        String reason = card.getBank().checkAuthorization(card, amount);
        if (reason != null) {
            status = "DECLINED";
            lastMessage = reason;
            return null;
        }
        status = "AUTHORIZED";
        lastMessage = "Approved";
        return "AUTH" + (100000 + random.nextInt(900000));
    }

    /** Debits the credit limit once authorization has been received. */
    public boolean capture(CreditCard card, double amount) {
        if (!"AUTHORIZED".equals(status)) return false;
        card.useCredit(amount);
        status = "CAPTURED";
        return true;
    }

    public void voidTransaction() { status = "VOIDED"; }
    public String getLastMessage() { return lastMessage; }
}
