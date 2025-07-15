package arcade;

public class ActiveGame extends ArcadeGame {

    private int minAge;

    public ActiveGame(String id, String gameTitle, int priceInPence, int minAge) throws InvalidGameIdException {
        super(id, gameTitle, priceInPence, minAge);
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public int calculatePrice(boolean peak) {
        int price = getPriceInPence();
        if (peak) {
            price = (int) (price * 1.5);
        }
        return price;
    }

    @Override
    public String toString() {
        return super.toString() + ", Min Age: " + minAge;
    }
}