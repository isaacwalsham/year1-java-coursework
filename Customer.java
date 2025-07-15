package arcade;

public class Customer {

    private String customerId;
    private String name;
    private int age;
    private int balance;
    private DiscountType discountType;

    public Customer(String customerId, String name, int age, DiscountType discountType) {
        balance = 0;
        this.customerId = customerId;
        this.name = name;
        this.age = age;
        this.discountType = discountType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getBalance() {
        return balance;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void addFunds(int amount) {
        balance += amount;
    }

    public void chargeAccount(ArcadeGame game, Boolean peak) throws InsufficientBalanceException, AgeLimitException {
        int price = game.calculatePrice(peak);

        if (balance < price) {
            throw new InsufficientBalanceException();
        }

        if (age < game.getMinAge()) {
            throw new AgeLimitException();
        }

        balance -= price;
    }
}