public class DamagePerTurnFast {
    private final int attack;
    private final int damage;
    private final int power;
    private final double STAB;
    private final double buff;
    private final int turns;
    private int dpt;
    public DamagePerTurnFast(int a, int d, int p, double s, double b, int t) {
        attack = a;
        damage = d;
        power = p;
        STAB = s;
        buff = b;
        turns = t;
    }

    public int findDPTFast() {
        dpt = (int)(((attack / (double)damage) * power * STAB * buff) / (turns + 1));
    return dpt;
    }
}
