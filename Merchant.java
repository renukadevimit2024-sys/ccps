class Merchant {
    private final String merchantId;
    private final String name;
    private final String category;

    public Merchant(String merchantId, String name, String category) {
        this.merchantId = merchantId;
        this.name = name;
        this.category = category;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
}
