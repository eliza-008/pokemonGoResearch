public class Mon {
    public final Pokemon poke;
    public final Move fast, charged1, charged2;
    public Move charged;
    public final double[] values =  new double[18];
    public int index = 0;
    public boolean pop = false;
    public double goodness = 0;
    private static int id = 1;

    public Mon(Pokemon p, Move f, Move c1, Move c2) {
        poke = p;
        fast = f;
        charged1 = c1;
        charged2 = c2;
    }

    //Gets called 18 times, once for each type matchup
    public void addVal(double value) {
        values[index] = value;
        index++;
    }

    public boolean isPop() {
        return pop;
    }

    //Returns the sum of sqrts of all 18 type matchups for the Pokemon
    public double goodness() {
        for (double v : values) {
            goodness += Math.sqrt(v);
        }
        return goodness;
    }

    public void printInfo() {
        poke.printInfo();
        System.out.print("   Fast move: ");
        fast.printInfo();
        System.out.print("   1st Charged: ");
        charged1.printInfo();
        System.out.print("   2nd Charged: ");
        charged2.printInfo();
        System.out.println("   Charged used: " + charged.name + " ");
    }

    public String getInfo() {
        return poke.getInfo() + " Fast: " + fast.getInfo() + " 1st Charged: " + charged1.getInfo()
                + " 2nd Charged: " + charged2.getInfo() + " Charged used: " + charged.name;
    }
}
