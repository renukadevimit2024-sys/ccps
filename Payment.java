class Payment {
    private final String paymentId;
    private final Transaction transaction;
    private final String paymentMode;
    private final String authCode;

    public Payment(String paymentId, Transaction transaction, String paymentMode, String authCode) {
        this.paymentId = paymentId;
        this.transaction = transaction;
        this.paymentMode = paymentMode;
        this.authCode = authCode;
    }

    public String getStatus() { return transaction.getStatus(); }
    public String getPaymentId() { return paymentId; }
    public String getAuthCode() { return authCode; }

    public void refund() {
        transaction.refund();
        transaction.getCard().releaseCredit(transaction.getAmount());
    }
}
