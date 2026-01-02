import java.util.Arrays;

public class Move {
    //Both fast moves and charged moves are modeled as Move objects, category differentiates the two
    public final String name, type;
    public String category;
    public final int id, power, energyGen, turns, cost;
    public final int[] statMods;
    public final double chanceMod;
    public double mAtkSelf = 1, mDefSelf = 1, mAtkOpp = 1, mDefOpp = 1;
    public int atkStageSelf, atkStageOpp, defStageSelf, defStageOpp;

    public Move(int i, String n, String t, int c, int m, int e, int mt, int ec, int[] v, double cs) {
        id = i;
        name = n;
        type = t;
        if (c == 0) {
            category = "Fast";
        }
        if (c == 1) {
            category = "Charged";
        }
        power = m;
        energyGen = e;
        turns = mt;
        cost = ec;
        statMods = v;
        chanceMod = cs;
    }

    public double[] multipliers() {
        mAtkSelf *= calcMultiplier(statMods[0], atkStageSelf); mDefSelf *= calcMultiplier(statMods[1], defStageSelf);
        mAtkOpp *= calcMultiplier(statMods[2], atkStageOpp); mDefOpp *= calcMultiplier(statMods[3], defStageOpp);
        return new double[] {mAtkSelf, mDefSelf, mAtkOpp, mDefOpp};
    }

    private double calcMultiplier(int s, int currStage) {
        if (currStage > 4) {currStage += s; return 2;}
        else if (currStage >= 0) {currStage += s; return 1 + 0.25 * s;}
        else if (currStage > -4) {currStage += s; return -4 / (double) (s - 4);}
        //Last two statements are when the current stage is <= -4
        else {currStage += s;}
        return 0.5;
    }

    public void printInfo() {
        if (category.equals("Fast")) {
            System.out.println(name + " #" + id + " Type: " + type + " Category: " + category +
                    " Power: " + power + " Energy Gen: " + energyGen + " Turns: " + turns);
        }
        else {
            System.out.println(name + " #" + id + " Type: " + type + " " + category + " move " +
                    " Power: " + power + " Cost: " + cost + " Stat Mods: " + Arrays.toString(statMods) +
                    " Chance of mods: " + chanceMod);
        }
    }

    public String getInfo() {
        String string;
        if (category.equals("Fast")) {
            string = name + " Id: " + id + " Type: " + type + " Category: " + category + " Power: " + power +
                    " Energy Generated: " + energyGen + " Turns: " + turns;
        }
        else {
            string = name + " Id: " + id + " Type: " + type + " Category: " + category + " Power: " + power + " Cost: "
                    + cost + " Stat Mods: " + Arrays.toString(statMods) + " Chance of stat mod: " + chanceMod;
        }
        return string;
    }
}
