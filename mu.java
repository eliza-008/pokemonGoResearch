public class mu {
    private int energyFast;
    private double energyCharged;
    private int fastMoveTurns;
    private double ratioUnshielded;
    private double mu;

    public mu(int f, int c, int t, double r) {
        energyFast = f;
        energyCharged = c;
        fastMoveTurns = t;
        ratioUnshielded = r;
    }

    public double getMu() {
        if (energyCharged < 100) {
            mu = energyFast / (energyCharged * fastMoveTurns);
        }
        if (energyCharged == 100) {
            mu = ratioUnshielded / (Math.ceil(energyFast / energyCharged) * fastMoveTurns);
        }
        return mu;
    }
}
