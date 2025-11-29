public class Ratio {
    private int lengthOfBattle;
    private double ratioCanShoot;
    private double ratioUnshielded;

    public Ratio(int l) {
        lengthOfBattle = l;
    }

    public double getRatioUnshielded() {
        ratioUnshielded = (lengthOfBattle * ratioCanShoot -(2 / 3.0)) / (lengthOfBattle * ratioCanShoot);
        return ratioUnshielded;
    }
}
