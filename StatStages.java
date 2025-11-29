public class StatStages {
    /*
    Define the stages of the pokemon's attack and defense
    The strength of attack and defense can change with certain charged moves
    Each time said charged moves are used, there's a chance that the stage changes
    They're buffs and debuffs that can work on a pokemon's attack or defense
    The idea is to make an object of this type and use the method getStatStage on it repeatedly
    Until the battle ends?
    */
    double probability;
    int changeInStage;
    int turnsToCharged;
    public StatStages(double p, int ds, int tca) {
        probability = p;
        changeInStage = ds;
        turnsToCharged = tca;
    }
    public double getStatStage(int l) {
        return probability * changeInStage * ((l * (l + 1)) / (2.0 * turnsToCharged) - (l + 1) / 2.0);
    }
}
