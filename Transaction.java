import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Transaction {
    private final String transactionId;
    private final CreditCard card;
    private final Merchant merchant;
    private final double amount;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String status = "PENDING";   // PENDING, SUCCESS, DECLINED, FAILED, REFUNDED
    private String reason = "";
    private String authCode = "";
    private boolean flagged = false;

    public Transaction(String transactionId, CreditCard card, Merchant merchant, double amount) {
        this.transactionId = transactionId;
        this.card = card;
        this.merchant = merchant;
        this.amount = amount;
    }

    public void initiate() { status = "PENDING"; }
    public void complete() { status = "SUCCESS"; }
    public void decline(String why) { status = "DECLINED"; reason = why; }
    public void fail(String why) { status = "FAILED"; reason = why; }
    public void cancel() { status = "FAILED"; reason = "Cancelled"; }
    public void refund() { status = "REFUNDED"; }

    public String getTransactionId() { return transactionId; }
    public CreditCard getCard() { return card; }
    public Merchant getMerchant() { return merchant; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public String getAuthCode() { return authCode; }
    public void setAuthCode(String code) { this.authCode = code; }
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    @Override
    public String toString() {
        String s = String.format("%s | %s | %s | $%.2f | %s%s | %s",
                transactionId, card.getMasked(), merchant.getName(), amount, status,
                flagged ? " (FLAGGED)" : "",
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return reason.isEmpty() ? s : s + " - " + reason;
    }
}
