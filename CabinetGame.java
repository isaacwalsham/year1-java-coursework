package arcade;

public class CabinetGame extends ArcadeGame {

    private Boolean payoutRewards;

    public CabinetGame(String id, String gameTitle, int priceInPence, int minAge, Boolean payoutRewards) throws InvalidGameIdException {
        super(id, gameTitle, priceInPence, minAge);
        this.payoutRewards = payoutRewards;
    }

    public Boolean getPayoutRewards() {
        return payoutRewards;
    }

    @Override
    public int calculatePrice(boolean peak) {
        int price = getPriceInPence();
        if (peak) {
            price = (int) (price * 1.25);
        }
        if (payoutRewards) {
            price += 20;
        }
        return price;
    }

    @Override
    public String toString() {
        return super.toString() + ", Payout Rewards: " + payoutRewards;
    }
}