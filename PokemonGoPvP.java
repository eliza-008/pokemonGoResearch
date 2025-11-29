public class PokemonGoPvP {
   public static void main(String[] args) {
       /*
       using random numbers to test if it works, could use different methods to read the values in practice
       attack1 - attacker's base attack stats
       defense2 - defender's base defense stats
       powerf - power of the fast move being used
       powerc - power of the charged move being used
       STAB - using random numbers for now
       typeAdvantage - using random numbers for now
       turnsf - duration of fast move
       energyf - how much energy the fast move generates each time
       energyc - energy cost of a single charged move
       energyGenerated - keeps track of energy generation
       turnsc - how long until the charged move can be used, rounded up
       probability - how likely it is that a stat change happens
       changeIn(Attack or Defense)Stage - how many stages an attack/defense buff/debuff jumps through
       (Attack or Defense)Multiplier: the current attack and defense multipliers

       need to decide between individual calculations for each pokemon in a team,
       or just two calculations, one for each team using averaged out values for everything
       My idea is given a 3v3, run the calculations once for each team being the attacker without taking damage
       See which team beats the other in the shortest amount of turns, treat that one as the winner
       */
       int attack1 = 50;
       int defense2 = 45;
       int powerf = 5;
       int powerc = 60;
       double STAB = 1.0;
       double typeAdvantage = 1.0;
       int turnsf = 1;
       int energyf = 5;
       int energyc = 70;
       int energyGenerated = 0;
       int turnsc = (int) Math.ceil(energyc / (double)energyf);
       double probability = 0.33;
       int changeInAttackStage = 2;
       int changeInDefenseStage = 1;
       double attackMultiplier;
       double defenseMultiplier;
       int hp = 110;
       int i;

       System.out.println("HP: " + hp);
       StatStages attackStat = new StatStages(probability, changeInAttackStage, turnsc);
       StatStages defenseStat = new StatStages(probability, changeInDefenseStage, turnsc);
       for (i = 0; hp > 0;) {
           attackMultiplier = attackStat.getStatStage(i);
           defenseMultiplier = defenseStat.getStatStage(i);
           energyGenerated += energyf;
           if ((int)(Math.ceil(energyc / (double)energyGenerated)) != 1) {
               hp -= ((int)Math.floor((attack1 * powerf * STAB * typeAdvantage * attackMultiplier) / (2 * defense2
                       * defenseMultiplier)) + 1);
               System.out.println("HP: " + hp);
               i += turnsf;
           }
           else {
               hp -= ((int)Math.floor((attack1 * powerc * STAB * typeAdvantage * attackMultiplier) / (2 * defense2
               * defenseMultiplier)) + 1);
               System.out.println("HP: " + hp);
               i += turnsc;
           }
       }
       System.out.println("The length of the battle was " + i + " turns.");

       /*
       Ideas to reduce the number of pokemon we analyze for our final results
       only use pokemon that are in their final evolutions, should reduce initial number of pokemon
       separate the pokemon into the 200 categories of type, and compare them within their types,
       as vectors, any vectors below a certain threshold plus a band will be thrown away
       vector: 14 * 3? 14 base types and 3 pokemon in team?
       teams: calculate between teams of 3 (with average values)
       L: assume level 50 pokemon, CM = combat multiplier
       */

       /*DamagePerTurnFast dmgf = new DamagePerTurnFast(attack1, defense2, powerf, STAB, typeAdvantage, turnsf);
       DamagePerTurnCharged dmgc = new DamagePerTurnCharged(attack1, defense2, powerc, STAB, typeAdvantage, turnsc);
       mu m = new mu(energyf, energyc, turnsc, getRatioUnshielded());
       int totalDamagePerTurn =
        */
   }
}
