package arcade;

public class VirtualRealityGame extends ActiveGame {

    private EquipmentType equipmentType;

    public VirtualRealityGame(String id, String gameTitle, int priceInPence, int minAge, EquipmentType equipmentType) throws InvalidGameIdException {
        super(id, gameTitle, priceInPence, minAge);
        this.equipmentType = equipmentType;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    @Override
    public int calculatePrice(boolean peak) {
        int price = super.calculatePrice(peak);

        switch (equipmentType) {
            case HEADSETONLY:
                break;
            case HEADSETANDCONTROLLER:
                price += 100;
                break;
            case FULLBODYTRACKING:
                price += 250;
                break;
        }
        return price;
    }

    @Override
    public String toString() {
        return super.toString() + ", Equipment Type: " + equipmentType;
    }
}