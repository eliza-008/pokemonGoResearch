public class DamagePerTurnCharged {
    /*damage should be the base damage stat of the pokemon defending
    mu = ratio of turns that the charged moved can dispel (how many fast attacks it takes
    to charge a charged move
    total damage = dptFast + (mu * charged)
    */
    private int attack;
    private int damage;
    private int power;
    private double STAB;
    private double buff;
    private int dmg;
    public DamagePerTurnCharged(int a, int d, int p, double s, double b, int t) {
        attack = a;
        damage = d;
        power = p;
        STAB = s;
        buff = b;
    }

    public int findDPTCharged() {
        dmg = (int)((attack / damage) * power * STAB * buff);
        return dmg;
    }
}
