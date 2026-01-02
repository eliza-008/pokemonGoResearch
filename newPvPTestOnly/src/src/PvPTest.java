import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class PvPTest {
    public static void main(String[] args) throws Exception {
        //Store info from csv file columns into their own ArrayLists, casting info to correct types
        ArrayList<Integer> moveIds = new ArrayList<>(), energyBoost = new ArrayList<>(), isCharged = new ArrayList<>(),
                moveTurns = new ArrayList<>();
        ArrayList<String> moveNames = new ArrayList<>(), moveTypes = new ArrayList<>(), movePower = new ArrayList<>(),
                energyCost = new ArrayList<>(), statMod = new ArrayList<>();
        ArrayList<Double> chance = new ArrayList<>();
        //Download related csv file and replace the file name in quotes with the complete file path
        Reader in1 = new FileReader("movesData.csv");
        Iterable<CSVRecord> records1 = CSVFormat.RFC4180.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(in1);
        for (CSVRecord record : records1) {
            moveIds.add(Integer.parseInt(record.get(0)));
            moveNames.add(record.get(1));
            moveTypes.add(record.get(2));
            isCharged.add(Integer.parseInt(record.get(3)));
            movePower.add(record.get(4));
            energyBoost.add(Integer.parseInt(record.get(5)));
            moveTurns.add(Integer.parseInt(record.get(6)));
            energyCost.add(record.get(7));
            statMod.add(record.get(8));
            chance.add(Double.parseDouble(record.get(9)));
        }
        in1.close();

        ArrayList<String> pokeName = new ArrayList<>(), pokeTypes = new ArrayList<>(),
                pokeFastMoves = new ArrayList<>(), pokeChargedMoves = new ArrayList<>();
        ArrayList<Integer> pokeNum = new ArrayList<>(), pokeStamina = new ArrayList<>(),
                pokeAttack = new ArrayList<>(), pokeDefense = new ArrayList<>();
        //Download related csv file and replace the file name in quotes with the complete file path
        Reader in3 = new FileReader("new_pokemon_data.csv");
        Iterable<CSVRecord> records3 = CSVFormat.RFC4180.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(in3);
        for (CSVRecord record : records3) {
            pokeNum.add(Integer.parseInt(record.get(0)));
            pokeName.add(record.get(1));
            pokeTypes.add(record.get(2));
            pokeAttack.add(Integer.parseInt(record.get(3)));
            pokeDefense.add(Integer.parseInt(record.get(4)));
            pokeStamina.add(Integer.parseInt(record.get(5)));
            pokeFastMoves.add(record.get(6));
            pokeChargedMoves.add(record.get(7));
        }
        in3.close();

        ArrayList<Integer> unavailableIds = new ArrayList<>();
        ArrayList<String> unavailableNames = new ArrayList<>(), unavailableTypes = new ArrayList<>();
        //Download related csv file and replace the file name in quotes with the complete file path
        Reader in4 = new FileReader("unavailable_pokemon.csv");
        Iterable<CSVRecord> records4 = CSVFormat.RFC4180.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(in4);
        for (CSVRecord record : records4) {
            unavailableIds.add(Integer.parseInt(record.get(0)));
            unavailableNames.add(record.get(1));
            unavailableTypes.add(record.get(2));
        }
        in4.close();

        //The ArrayList stores all the Move objects created
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < moveIds.size(); i++) {
            int moveId = moveIds.get(i), ischarged = isCharged.get(i), movepower, energycost,
                    energyboost = energyBoost.get(i), moveturns = moveTurns.get(i), index = 0;
            String moveName = moveNames.get(i), moveType = moveTypes.get(i);
            //Unreleased moves don't have energy cost or power listed
            try {
                movepower = Integer.parseInt(movePower.get(i));
            } catch (NumberFormatException e) {
                movepower = 0;
            }
            try {
                energycost = Integer.parseInt(energyCost.get(i));
            } catch (NumberFormatException e) {
                energycost = 0;
            }
            //Turn the String into an array
            String[] stats1 = bracketedStringToArray(statMod.get(i));
            int[] statModValues = new int[4];
            //Leave out speed and opponent speed stat modifiers, make debuff stat mods negative
            for (int j = 0; j < 6; j++) {
                if (j != 2 && index != 4) {
                    statModValues[index] = Integer.parseInt(stats1[j].trim());
                    if (index == 2 | index == 3) {
                        statModValues[index] *= -1;
                    }
                    if (moveName.equals("Draco Meteor") | moveName.equals("Leaf Storm") | moveName.equals("Overheat")
                            | moveName.equals("Psycho Boost") | moveName.equals("Superpower")) {
                        statModValues[0] *= -1;
                    }
                    if (moveName.equals("Brave Bird") | moveName.equals("Clanging Scales")
                            | moveName.equals("Close Combat") | moveName.equals("Dragon Ascent")
                            | moveName.equals("High Jump Kick") | moveName.equals("Superpower")
                            | moveName.equals("V-create") | moveName.equals("Volt Tackle")
                            | moveName.equals("Wild Charge")) {
                        statModValues[1] *= -1;
                    }
                    ++index;
                }
            }
            double chanceStatMod = chance.get(i);
            moves.add(new Move(moveId, moveName, moveType, ischarged, movepower, energyboost, moveturns, energycost,
                    statModValues, chanceStatMod));
        }

        //Remove moves without power or energy cost listed
        moves.removeIf(move -> move.power == 0);
        moves.removeIf(move -> move.category.equals("Charged") && move.cost == 0);

        //The ArrayList stores all the Pokemon objects created
        ArrayList<Pokemon> pokemons = new ArrayList<>();
        for (int i = 0; i < pokeName.size(); i++) {
            String name = pokeName.get(i);
            int pokedexNum = pokeNum.get(i), stamina = pokeStamina.get(i), attack = pokeAttack.get(i),
                    defense = pokeDefense.get(i);
            String type1 = pokeTypes.get(i), type2 = "null";
            if (type1.contains(", ")) {
                String[] typesArray = type1.split(", ");
                type1 = typesArray[0];
                type2 = typesArray[1];
            }
            //Takes a string, turns it into a String array - method at the end of main()
            String[] fastMoves, chargedMoves;
            if (!pokeFastMoves.get(i).isEmpty()) {
                fastMoves = stringToArray(pokeFastMoves.get(i));
            } else fastMoves = new String[]{""};
            if (!pokeChargedMoves.get(i).isEmpty()) {
                chargedMoves = stringToArray(pokeChargedMoves.get(i));
            } else chargedMoves = new String[]{""};
            pokemons.add(new Pokemon(name, pokedexNum, type1, type2, stamina, attack, defense, fastMoves, chargedMoves,
                    moves));
        }

        //Make objects for unavailable pokemon, just to compare
        ArrayList<Pokemon> unavailablePokes = new ArrayList<>();
        for (int i = 0; i < unavailableIds.size(); i++) {
            String name = unavailableNames.get(i), type1 = unavailableTypes.get(i), type2 = "null";
            int id = unavailableIds.get(i);
            if (type1.contains(", ")) {
                String[] typesArray = type1.split(", ");
                type1 = typesArray[0];
                type2 = typesArray[1];
            }
            unavailablePokes.add(new Pokemon(name, id, type1, type2));
        }

        //Remove Pokemon that are not available
        for (int i = 0; i < pokemons.size(); i++) {
            for (Pokemon poke : unavailablePokes) {
                if (pokemons.get(i).name.equals(poke.name) && pokemons.get(i).type1.equals(poke.type1)
                        && pokemons.get(i).type2.equals(poke.type2)) {
                    pokemons.remove(i);
                }
            }
        }

        //Remove Pokemon whose movesets aren't known (also gets rid of Megas)
        pokemons.removeIf(poke -> poke.fastMoves.isEmpty() | poke.chargedMoves.isEmpty());

        //Make individual lists for fast moves and charged moves - for information purposes, not necessary
        ArrayList<Move> fastMoves = new ArrayList<>(), chargedMoves = new ArrayList<>();
        for (Move move : moves) {
            if (move.category.equals("Fast")) {
                fastMoves.add(move);
            } else chargedMoves.add(move);
        }

        System.out.println("Current number of Pokemon: " + pokemons.size());

        String[] types = {"Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel",
                "Fire", "Water", "Grass", "Electric", "Psychic", "Ice", "Dragon", "Dark", "Fairy"};
        /*Rows: attacker's type, columns: defender's type (order based on the array above).
        Ex. normal on rock: typeAdvs[0][5] = 0.625 (normal's damage to rock types*/
        double[][] typeAdvs = {{1, 1, 1, 1, 1, 0.625, 1, 0.391, 0.625, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1.6, 1, 0.625,
                0.625, 1, 1.6, 0.625, 0.391, 1.6, 1, 1, 1, 1, 0.625, 1.6, 1, 1.6, 0.625}, {1, 1.6, 1, 1, 1, 0.625, 1.6,
                1, 0.625, 1, 1, 1.6, 0.625, 1, 1, 1, 1, 1}, {1, 1, 1, 0.625, 0.625, 0.625, 1, 0.625, 0.391, 1, 1, 1.6,
                1, 1, 1, 1, 1, 1.6}, {1, 1, 0.391, 1.6, 1, 1.6, 0.625, 1, 1.6, 1.6, 1, 0.625, 1.6, 1, 1, 1, 1, 1}, {1,
                0.625, 1.6, 1, 0.625, 1, 1.6, 1, 0.625, 1.6, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 0.625, 0.625, 0.625, 1, 1, 1,
                0.625, 0.625, 0.625, 1, 1.6, 1, 1.6, 1, 1, 1.6, 0.625}, {0.391, 1, 1, 1, 1, 0.625, 1, 1.6, 0.625, 1, 1,
                1, 1, 1.6, 1, 1, 0.625, 1}, {1, 1, 1, 1, 1, 1.6, 1, 1, 0.625, 0.625, 0.625, 1, 0.625, 1, 1.6, 1, 1,
                1.6}, {1, 1, 1, 1, 1, 0.625, 1.6, 1, 1.6, 0.625, 0.625, 1.6, 1, 1, 1.6, 0.625, 1, 1}, {1, 1, 1, 1, 1.6,
                1.6, 1, 1, 0.625, 1.6, 0.625, 0.625, 1, 1, 1, 0.625, 1, 1}, {1, 1, 0.625, 0.625, 1.6, 1.6, 0.625, 1,
                0.625, 0.625, 0.625, 1.6, 0.625, 1, 1, 0.625, 1, 1}, {1, 1, 1.6, 1, 0.391, 0.625, 1, 1, 1, 1, 1.6,
                0.625, 0.625, 1, 1, 0.625, 1, 1}, {1, 1.6, 1, 1.6, 1, 1, 1, 1, 0.625, 1, 1, 1, 1, 0.625, 1, 1, 0.391,
                1}, {1, 1, 1.6, 1, 1.6, 1, 1, 1, 0.625, 0.625, 0.625, 1.6, 1, 1, 0.625, 1.6, 1, 1}, {1, 1, 1, 1, 1, 1,
                1, 1, 0.625, 1, 1, 1, 1, 1, 1, 1.6, 1, 0.391}, {1, 0.625, 1, 1, 1.6, 1, 1, 1.6, 1, 1, 1, 1, 1, 1.6, 1,
                1, 0.625, 0.625}, {1, 1.6, 1, 0.625, 1, 0.625, 1, 1, 0.625, 0.625, 1, 1, 1, 1, 1, 1.6, 1.6, 1}};

        /*To treat each Pokemon with a different moveset as separate, make Mon type objects
        These objects are initialized with a Pokemon object and three Move objects (one fast, two charged)*/
        ArrayList<Mon> mons = new ArrayList<>();
        int numToRemove = 0;
        for (int i = 0; i < types.length; i++) {
            for (int j = i + 1; j < types.length; j++) {
                for (Pokemon poke : pokemons) {
                    //Handles Pokemon with one type
                    if (poke.type1.equals(types[i])) {
                        /*Both i and this index are used later to calculate type advantages in matchups
                        pokeIndex2 is initially out of bounds for the types array on purpose; handles Pokemon with a
                        single type for type advantages*/
                        int pokeIndex2 = 18;
                        //This if statement is to avoid copying and pasting everything inside a second time
                        if (poke.type2.equals("null") | poke.type2.equals(types[j])) {
                            //For 2 type Pokemon, both types need to be considered for type advantage multipliers later
                            if (poke.type2.equals(types[j])) {
                                pokeIndex2 = j;
                            }
                            //Iterate through all move combinations for the Pokemon (1 fast, 2 charged, no repeats)
                            for (Move fast : poke.fastMoves) {
                                for (int i1 = 0; i1 < poke.chargedMoves.size(); i1++) {
                                    for (int j1 = i1 + 1; j1 < poke.chargedMoves.size(); j1++) {
                                        Move charged1 = poke.chargedMoves.get(i1), charged2 = poke.chargedMoves.get(j1);
                                        //Initialize the object that stores the Pokemon and its moves
                                        Mon mon = new Mon(poke, fast, charged1, charged2);
                                /*Loop runs once for each Pokemon type, so 18 times. Values stored in an array inside
                                the Mon object, which is later used for the heuristic's "goodness" metric*/
                                        for (int k1 = 0; k1 < types.length; k1++) {
                                            double f_STAB = 1, c1_STAB = 1, c2_STAB = 1, dpt = 0;
                                            //For moves, STAB = 1.2 if the type of the attack and the pokemon using it
                                            //are of the same type (both are ice, etc.)
                                            if (fast.type.equals(poke.type1) | fast.type.equals(poke.type2)) {
                                                f_STAB = 1.2;
                                            }
                                            if (charged1.type.equals(poke.type1) | charged1.type.equals(poke.type2)) {
                                                c1_STAB = 1.2;
                                            }
                                            if (charged2.type.equals(poke.type1) | charged2.type.equals(poke.type2)) {
                                                c2_STAB = 1.2;
                                            }
                                            double typeAdv = typeAdvs[i][k1];
                                            //Combines both type advantages into a single variable for dual type Pokemon
                                            if (pokeIndex2 < 18) {
                                                typeAdv *= typeAdvs[pokeIndex2][k1];
                                            }
                                            double dpt_f = poke.attack * fast.power * typeAdv * (f_STAB / 2)
                                                    + 0.5;
                                            dpt += dpt_f;
                                            double dpt_c1, dpt_c2;
                                            double dmg_c1 = poke.attack * charged1.power * typeAdv * (c1_STAB / 2) + 0.5;
                                            double dmg_c2 = poke.attack * charged2.power * typeAdv * (c2_STAB / 2) + 0.5;
                                            if (charged1.cost == 100) {
                                                dpt_c1 = (1 / (Math.ceil(charged1.cost / (double) fast.energyGen) * fast.turns))
                                                        * dmg_c1;
                                            } else {
                                                dpt_c1 = (fast.energyGen * dmg_c1) / ((double) (charged1.cost * fast.turns));
                                            }
                                            if (charged2.cost == 100) {
                                                dpt_c2 = (1 / (Math.ceil(charged2.cost / (double) fast.energyGen) * fast.turns))
                                                        * dmg_c2;
                                            } else {
                                                dpt_c2 = (fast.energyGen * dmg_c2) / ((double) (charged2.cost * fast.turns));
                                            }
                                            dpt += Math.max(dpt_c1, dpt_c2);
                                            //Saving the charged move used for the dpt calculation
                                            if (dpt_c1 < dpt_c2) {
                                                mon.charged = charged2;
                                            } else {
                                                mon.charged = charged1;
                                            }
                                            //Add the D*S*DPT value to a double array inside mon
                                            mon.addVal(poke.defense * poke.stamina * dpt);
                                        }
                                        //Add the Mon object to an ArrayList
                                        mons.add(mon);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Initial number of mons: " +  mons.size());

        //Mark the Pokemon that are worse than others of the same type
        for (int i = 0; i < mons.size(); i++) {
            for (int j = i + 1; j < mons.size(); j++) {
                //Check if both Pokemon share at least one type to compare them
                Mon mon1 = mons.get(i), mon2 = mons.get(j);
                if (mon1.poke.type1.equals(mon2.poke.type1) | (mon1.poke.type1.equals(mon2.poke.type2))
                        | mon1.poke.type2.equals(mon2.poke.type1) | (mon1.poke.type2.equals(mon2.poke.type2)
                        && !(mon1.poke.type2.equals("null")))) {
                    //Retrieving and comparing the values that were stored after each type matchup
                    //If first mon's values < 0.7 * 2nd mon's values at any point, mark first mon for removal, exit loop
                    for (int k = 0; k < mon1.values.length; k++) {
                        if (mon1.values[k] < 0.7 * mon2.values[k]) {
                            mon1.pop = true;
                            break;
                        }
                    }
                    if (mon1.pop) {
                        numToRemove++;
                        break;
                    }
                }
            }
        }

        //Remove the Pokemon marked for removal
        mons.removeIf(Mon::isPop);

        //Sort mons from best to worst (based on the sum of sqrts of all 18 type matchups)
        //LegacyMergeSort needed for my JDK version
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        mons.sort(Comparator.comparingDouble(Mon::goodness).reversed());

        System.out.println("Number of mons after removing: " + mons.size());

        //Keep Pokemon with their best movesets only
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < mons.size(); i++) {
                for (int j = i + 1; j < mons.size(); j++) {
                    if (mons.get(j).poke.name.equals(mons.get(i).poke.name)) {
                        mons.remove(j);
                    }
                }
            }
        }

        System.out.println("Number of mons with their best movesets: " + mons.size());

        final String FILE_NAME = "1v1_results.csv";
        final String[] HEADERS = {"Rank", "Pokemon", "#", "Type", "Attack", "Defense", "Stamina", "Fast move", "1st Charged move",
        "2nd Charged move", "Best Charged move"};
        // Data to write (can be a List<List<Object>> or List<Object[]> or List<YourObject>)
        List<Object[]> monsInfo = new ArrayList<>();
        int rankNum = 1;
        for (Mon mon : mons) {
            Object[] monInfo = new Object[HEADERS.length];
            monInfo[0] = rankNum;
            monInfo[1] = mon.poke.name;
            monInfo[2] = mon.poke.id;
            if (!mon.poke.type2.equals("null")) {
                monInfo[3] = mon.poke.type1 + ", " + mon.poke.type2;
            }
            else {
                monInfo[3] = mon.poke.type1;
            }
            monInfo[4] = mon.poke.attack;
            monInfo[5] = mon.poke.defense;
            monInfo[6] = mon.poke.stamina;
            monInfo[7] = mon.fast.name;
            monInfo[8] = mon.charged1.name;
            monInfo[9] = mon.charged2.name;
            monInfo[10] = mon.charged.name;
            monsInfo.add(monInfo);
            rankNum++;
        }
        CSVFormat format = CSVFormat.EXCEL.builder()
                .setHeader(HEADERS).get();
        // Use try-with-resources to ensure the writer and printer are closed automatically
        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_NAME));
                CSVPrinter csvPrinter = new CSVPrinter(writer, format)
        )
        {// Write individual records
            for (Object[] record : monsInfo) {
                csvPrinter.printRecord(record);
            }
            // The writer is automatically flushed and closed by the try-with-resources block
            System.out.println("CSV file created successfully at: " + FILE_NAME);

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        //for reference only, shows how our 16 results are ordered
        String[][] solutions = {{"atkStage > 4 & defStage > 4", "atkStage > 4 & 0 <= defStage <= 4",
                "atkStage > 4 & -4 < defStage < 0", "atkStage > 4 & defStage <= -4"}, {"0 <= atkStage <= 4 & defStage > 4",
                "0 <= atkStage <= 4 & 0 <= defStage <= 4", "0 <= atkStage <= 4 & -4 < defStage < 0", "0 <= atkStage <= 4" +
                " & defStage <= -4"}, {"-4 < atkStage < 0 & defStage > 4", "-4 < atkStage < 0 & 0 <= defStage <= 4",
                "-4 < atkStage < 0 & -4 < defStage < 0", "-4 < atkStage < 0 & defStage <= -4"}, {"atkStage <= -4 " +
                "& defStage > 4", "atkStage <= -4 & 0 <= defStage <= 4", "atkStage <= -4 & -4 < defStage < 0",
                "atkStage <= -4 & defStage <= -4"}};

        List<String> threeVThreeData = new ArrayList<>();
        //3v3 modeling more combinations, making sure there are no duplicates on either team
        for (Mon mon1 : mons) {
            for (Mon mon2 : mons) {
                for (Mon mon3 : mons) {
                    for (Mon mon4 : mons) {
                        for (Mon mon5 : mons) {
                            for (Mon mon6 : mons) {
                                //Ensures each team does not have duplicate Pokemon, variants allowed
                                if (!(Objects.equals(mon1.poke, mon2.poke))
                                        && !(Objects.equals(mon2.poke, mon3.poke))
                                        && !(Objects.equals(mon4.poke, mon5.poke))
                                        && !(Objects.equals(mon5.poke, mon6.poke))) {
                                    Mon[] v = new Mon[]{mon1, mon2, mon3}, w = new Mon[]{mon4, mon5, mon6};
                                    int[][] twoSetsOfL = setsOfL(v, w, types, typeAdvs);
                                    int[] vL = twoSetsOfL[0], wL = twoSetsOfL[1];
                                    int longestVL = vL[0], longestWL = wL[0];
                                    for (Integer L : vL) {
                                        if (L > longestVL) {
                                            longestVL = L;
                                        }
                                    }
                                    for (Integer L : wL) {
                                        if (L > longestWL) {
                                            longestWL = L;
                                        }
                                    }
                                    if (longestVL > longestWL) {
                                        threeVThreeData.add("Winning team: " + v[0].poke.name + ", " + v[1].poke.name +
                                                ", " + v[2].poke.name + " Losing team: " + w[0].poke.name + ", "
                                                + w[1].poke.name + ", " + w[2].poke.name);
                                    } else {
                                        threeVThreeData.add("Winning team: " + w[0].poke.name + ", " + w[1].poke.name
                                                + ", " + w[2].poke.name + " Losing team: " + v[0].poke.name + ", "
                                                + v[1].poke.name + ", " + v[2].poke.name);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final String FILE_NAME1 = "3v3_results.csv";
        // Data to write (can be a List<List<Object>> or List<Object[]> or List<YourObject>)
        List<Object> objectsList = new ArrayList<>(threeVThreeData);

        // Use try-with-resources to ensure the writer and printer are closed automatically
        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_NAME1));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL)
        )
        {// Write individual records
            for (Object record : objectsList) {
                csvPrinter.printRecord(record);
            }
            // The writer is automatically flushed and closed by the try-with-resources block
            System.out.println("CSV file created successfully at: " + FILE_NAME1);

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    //To avoid having two sets of the same process to compute L
    public static int[][] setsOfL(Mon[] v, Mon[] w, String[] types,
                                      double[][] typeAdvs) {
        int[] vL = new int[9], wL = new int[9];
        int[][] array = {vL, wL};
        for (int i = 0; i < 3; i++) {
            int indexvL = 0, indexwL = 0;
            for (int j = 0; j < 3; j++) {
                //lPrime is a simplified version of L needed for stat buffs
                int lPrimeW = lPrime(v[i], w[j], types, typeAdvs), lPrimeV = lPrime(w[j], v[i], types, typeAdvs), lPrime;
                lPrime = Math.min(lPrimeW, lPrimeV);
                int atkStageVi = atkStatStage(v[i], w[j], lPrime), atkStageWj = atkStatStage(w[j], v[i], lPrime);
                int defStageVi = defStatStage(v[i], w[j], lPrime), defStageWj = defStatStage(w[j], v[i], lPrime);
                double atkMultVi = statMultiplier(atkStageVi), atkMultWj = statMultiplier(atkStageWj);
                double defMultVi = statMultiplier(defStageVi), defMultWj = statMultiplier(defStageWj);
                //How long until mon vi is defeated
                int lV = lengthOfBattle(w[j], v[i], atkMultWj, defMultVi, atkStageWj, defStageVi, lPrime, types, typeAdvs);
                //How long until mon wj is defeated
                int lW = lengthOfBattle(v[i], w[j], atkMultVi, defMultWj, atkStageVi, defStageWj, lPrime, types, typeAdvs);
                vL[indexvL] = lV; wL[indexwL] = lW;
                indexvL++;
                indexwL++;
            }
        }
        return array;
    }
    //simplified model of how long it takes to defeat mon2, for use in the stat stage function
    public static int lPrime(Mon mon1, Mon mon2, String[] types, double[][] typeAdvs) {
        Pokemon poke1 = mon1.poke, poke2 = mon2.poke;
        Move fast = mon1.fast, charged1 = mon1.charged, charged2 = mon2.charged;
        String type1 = poke1.type1, type2 = poke1.type2, type3 = poke2.type1, type4 = poke2.type2, fastType = fast.type,
                chargedType = charged1.type;
        int energy_f = fast.energyGen, energy_c = charged1.cost, turns_f = fast.turns, stam = poke1.stamina,
                atk = poke1.attack, def = poke2.defense, p_f = fast.power, p_c = charged1.power, lPrime;
        double stab_f = 1, stab_c = 1, typeAdv = typeAdvantage(type1, type2, type3, type4, types, typeAdvs);
        if (Objects.equals(type1, fastType) | Objects.equals(type2, fastType)) {stab_f = 1.2;}
        if (Objects.equals(type1, chargedType) | Objects.equals(type2, chargedType)) {stab_c = 1.2;}
        int dmg_f = (int) Math.floor((atk/(double) def) * p_f * stab_f * typeAdv);
        int dmg_c = (int) Math.floor((atk/(double) def) * p_c * stab_c * typeAdv);
        if (energy_c == 100) {
            lPrime = (int) Math.ceil(stam / ((dmg_f /(double) fast.turns) + (1 / (Math.floor(energy_c /(double) energy_f)
                    * fast.turns)) * dmg_c));
        }
        else {
            lPrime = (int) Math.floor(stam / ((dmg_f /(double) fast.turns) + (energy_f /(double)(energy_c * fast.turns))
                    * dmg_c));
        }
        return lPrime;
    }
    public static double typeAdvantage(String type1, String type2, String type3, String type4, String[] types,
                                       double[][] typeAdvs) {
        int index1 = 18, index2 = 18, index3 = 18, index4 = 18;
        double typeAdv = 1;
        for (int i = 0; i < types.length; i++) {
            if (type1.equals(types[i])) {index1 = i;}
            if (type2.equals(types[i])) {index2 = i;}
            if (type3.equals(types[i])) {index3 = i;}
            if (type4.equals(types[i])) {index4 = i;}
        }
        if (index1 < 18 && index3 < 18) {typeAdv *= typeAdvs[index1][index3];}
        if (index1 < 18 && index4 < 18) {typeAdv *= typeAdvs[index1][index4];}
        if (index2 < 18 && index3 < 18) {typeAdv *= typeAdvs[index2][index3];}
        if (index2 < 18 && index4 < 18) {typeAdv *= typeAdvs[index2][index4];}
        return typeAdv;
    }
    public static int atkStatStage(Mon mon1, Mon mon2, int lPrime) {
        //average of the effects from mon1 and mon2's charged moves
        return (int) Math.floor(((mon1.charged.chanceMod * mon1.charged.multipliers()[0] * ((Math.pow(lPrime, 2)
                + lPrime) / (2 * (mon1.charged.cost / (double)(mon1.fast.energyGen * mon1.fast.turns)))
                - (lPrime + 1) / 2.0)) + (mon2.charged.chanceMod * mon2.charged.multipliers()[2] * ((Math.pow(lPrime, 2)
                + lPrime) / (2 * (mon2.charged.cost / (double)(mon2.fast.energyGen * mon2.fast.turns)))
                - (lPrime + 1) / 2.0))) / 2.0);
    }
    public static int defStatStage(Mon mon1, Mon mon2, int lPrime) {
        //average of the effects from mon1 and mon2's charged moves
        return (int) Math.floor(((mon1.charged.chanceMod * mon1.charged.multipliers()[1] * ((Math.pow(lPrime, 2)
                + lPrime) / (2 * (mon1.charged.cost / (double)(mon1.fast.energyGen * mon1.fast.turns)))
                - (lPrime + 1) / 2.0)) + (mon2.charged.chanceMod * mon2.charged.multipliers()[3] * ((Math.pow(lPrime, 2)
                + lPrime) / (2 * (mon2.charged.cost / (double)(mon2.fast.energyGen * mon2.fast.turns)))
                - (lPrime + 1) / 2.0))) / 2.0);
    }
    public static double statMultiplier(int statStage) {
        double multiplier = 1;
        if (statStage > 4) {multiplier = 2;}
        if (statStage >= 0 && statStage <= 4) {multiplier += 0.25 * statStage;}
        if (statStage > -4 && statStage < 0) {multiplier = -(statStage / (double)(statStage - 4));}
        if (statStage <= -4) {multiplier = 0.5;}
        return multiplier;
    }
    public static double STAB(Mon mon, Move move) {
        double stab = 1;
        if (Objects.equals(mon.poke.type1, move.type) | Objects.equals(mon.poke.type2, move.type)) {stab = 1.2;}
        return stab;
    }

    //The method primarily called to find the length L of battles, accounts for all regions
    public static int lengthOfBattle(Mon mon1, Mon mon2, double mAtk, double mDef, int atkStage, int defStage, int lPrime,
                                     String[] types, double[][] typeAdvs) {
        int length = 0;
        //Regions 6, 10, 11 (Where the matlab solver had no solutions)
        if ((atkStage >= 0 && atkStage <= 4 && defStage >= 0 && defStage <= 4) | (atkStage > -4 && atkStage < 0
                && defStage >= 0 && defStage <= 4) | (atkStage > -4 && atkStage < 0 && defStage > -4 && defStage < 0)) {
            double typeAdv = typeAdvantage(mon1.poke.type1, mon1.poke.type2, mon2.poke.type1, mon2.poke.type2, types,
                    typeAdvs);
            int dmg_f = (int) Math.floor(((mon1.poke.attack * mAtk) / (mon2.poke.defense * mDef) * mon1.fast.power
                    * STAB(mon1, mon1.fast) * typeAdv));
            int dmg_c = (int) Math.floor(((mon1.poke.attack * mAtk) / (mon2.poke.defense * mDef) * mon1.charged.power
                    * STAB(mon1, mon2.fast) * typeAdv));
            double r_cs;
            if (mon1.charged.cost == 100) {
                r_cs = 1 / (Math.ceil(mon1.charged.cost / (double) mon1.fast.energyGen) * mon1.fast.turns);
            }
            else {
                r_cs = mon1.fast.energyGen / (double) (mon1.charged.cost * mon1.fast.turns);
            }
            double r_us = (lPrime * r_cs - 2/3.0) / (lPrime * r_cs);
            double mu = r_cs * r_us;
            double dpt_f = dmg_f / (double) mon1.fast.turns, dpt_c = mu * dmg_c;
            double dpt = dpt_f + dpt_c;
            length = (int) Math.floor(mon2.poke.stamina / dpt);}
        //Define all the variables used in the calculations below
        int def = mon2.poke.defense, hp = mon2.poke.stamina, tc = (int) Math.floor(mon1.charged.cost /
                (double)(mon1.fast.energyGen * mon1.fast.turns)), tf = mon1.fast.turns, atk = mon1.poke.attack,
                pf = mon1.fast.power, pc = mon1.charged.power;
        double dA = mon1.charged.multipliers()[0], dB = mon1.charged.multipliers()[3], pa = mon1.charged.chanceMod,
                pb = mon1.charged.chanceMod;
        //3, 5, 7, 8, 15: Solve for L for regions with solutions in the form of (P(z), z, k), where z is L
        //Region 3
        if (atkStage > 4 && defStage > -4 && defStage < 0) {
            //Coefficients of the polynomial's terms are used to get L
            double a = atk*dB*pb*pf*tc + atk*dB*pb*pc*tf;
            double b = - atk*dB*pb*pf*tc*tc - atk*dB*pb*pc*tc*tf + atk*dB*pb*pf*tc + atk*dB*pb*pc*tf;
            double c = - atk*dB*pb*pf*tc*tc - atk*dB*pb*pc*tc*tf - 8*atk*pf*tc*tc - 8*atk*pc*tc*tf;
            double d = + 8*def*hp*tc*tc*tf;
            Solve solver = new Solve(d, c, b, a);
            length = solver.getSolution();
        }
        //Region 5
        if (atkStage >= 0 && atkStage <= 4 && defStage > 4) {
            double a = atk*dA*pa*pf*tc + atk*dA*pa*pc*tf;
            double b = - atk*dA*pa*pf*tc*tc - atk*dA*pa*pc*tc*tf + atk*dA*pa*pf*tc + atk*dA*pa*pc*tf;
            double c =  - atk*dA*pa*pf*tc*tc - atk*dA*pa*pc*tc*tf + 8*atk*pf*tc*tc + 8*atk*pc*tc*tf;
            double d = - 32*def*hp*tc*tc*tf;
            Solve solver = new Solve(d, c, b, a);
            length = solver.getSolution();
        }
        //Region 7
        if (atkStage >= 0 && atkStage <= 4 && defStage > -4 && defStage < 0) {
            double a = atk*dA*pa*pf*tc + atk*dA*pa*pc*tf;
            double b = - atk*dA*pa*pf*tc*tc - atk*dA*pa*pc*tc*tf + atk*dA*pa*pf*tc + atk*dA*pa*pc*tf;
            double c = - atk*dA*pa*pf*tc*tc - atk*dA*pa*pc*tc*tf + 8*atk*pf*tc*tc + 8*atk*pc*tc*tf;
            double d = - 8*def*hp*tc*tc*tf;
            Solve solver = new Solve(d, c, b, a);
            length = solver.getSolution();

        }
        //Region 8
        if (atkStage >= 0 && atkStage <= 4 && defStage <= -4) {
            double a = atk*dA*pa*pf*tc + atk*dA*pa*pc*tf;
            double b = - atk*dA*pa*pf*tc*tc - atk*dA*pa*pc*tc*tf + atk*dA*pa*pf*tc + atk*dA*pa*pc*tf;
            double c = - atk*dA*pa*pf*tc*tc - atk*dA*pa*pc*tc*tf + 8*atk*pf*tc*tc + 8*atk*pc*tc*tf;
            double d = - 8*def*hp*tc*tc*tf;
            Solve solver = new Solve(d, c, b, a);
            length = solver.getSolution();
        }
        //Region 15
        if (atkStage <= -4 && defStage <= -4) {
            double a = atk*dB*pb*pf*tc + atk*dB*pb*pc*tf;
            double b = - atk*dB*pb*pf*tc*tc - atk*dB*pb*pc*tc*tf + atk*dB*pb*pf*tc + atk*dB*pb*pc*tf;
            double c = - atk*dB*pb*pf*tc*tc - atk*dB*pb*pc*tc*tf - 8*atk*pf*tc*tc - 8*atk*pc*tc*tf;
            double d = 32*def*hp*tc*tc*tf;
            Solve solver = new Solve(d, c, b, a);
            length = solver.getSolution();
        }
        //Where the equations for L don't need anything extra done
        //Region 1
        if (atkStage > 4 && defStage > 4) {length = (2*def*hp*tc*tf)/(atk*pc*tf + atk*pf*tc);}
        //Region 2
        if (atkStage > 4 && defStage >= 0 && defStage <= 4) {
            int length1 = (int) Math.floor((Math.sqrt(64*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 128*Math.pow(atk,2)*pc*pf*tc*Math.pow(tc,2)*tf + 64*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2)
                    + 16*atk*dB*def*hp*pb*pc*tc*Math.pow(tc,2)*Math.pow(tf,2) - 16*atk*dB*def*hp*pb*pc*tc*tc*Math.pow(tf,2)
                    + 16*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf - 16*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*tc*tf
                    + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 2*Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*tc*Math.pow(tf,2)
                    + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    - 32*dB*Math.pow(def,2)*Math.pow(hp,2)*pb*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 8*atk*pc*tc*tf
                    + 8*atk*pf*tc*tc - dB*def*hp*pb*tc*tf + dB*def*hp*pb*tc*tc*tf) / (2*dB*def*hp*pb*tc*tf));
            int length2 = (int) Math.floor((8*atk*pc*tc*tf - Math.sqrt(64*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 128*Math.pow(atk,2)*pc*pf*tc*Math.pow(tc,2)*tf + 64*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2)
                    + 16*atk*dB*def*hp*pb*pc*tc*Math.pow(tc,2)*Math.pow(tf,2) - 16*atk*dB*def*hp*pb*pc*tc*tc*Math.pow(tf,2)
                    + 16*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf - 16*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*tc*tf
                    + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 2*Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*tc*Math.pow(tf,2)
                    + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    - 32*dB*Math.pow(def,2)*Math.pow(hp,2)*pb*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 8*atk*pf*tc*tc
                    - dB*def*hp*pb*tc*tf + dB*def*hp*pb*tc*tc*tf) / (2*dB*def*hp*pb*tc*tf));
            length = Math.min(length1, length2);
        }
        //Region 4
        if (atkStage > 4 && defStage <= -4) {length = (def*hp*tc*tf)/(2*atk*pc*tf + 2*atk*pf*tc);}
        //Region 9
        if (atkStage > -4 && atkStage < 0 && defStage > 4) {
            length = (int)-Math.floor((Math.sqrt(4*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 8*Math.pow(atk,2)*
                    pc*pf*tc*Math.pow(tc,2)*tf + 4*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2)
                    - 4*atk*dA*def*hp*pa*pc*tc*Math.pow(tc,2)*Math.pow(tf,2) + 4*atk*dA*def*hp*pa*pc*tc*tc*Math.pow(tf,2)
                    - 4*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf + 4*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*tc*tf
                    + Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2) *Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 2*Math.pow(dA,2)*Math.pow(def,2) *Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*tc*Math.pow(tf,2)
                    + Math.pow(dA,2)*Math.pow(def,2) *Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 32*dA*Math.pow(def,2)*Math.pow(hp,2) *pa*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 2*atk*pc*tc*tf
                    + 2*atk*pf*tc*tc + dA*def*hp*pa*tc*tf - dA*def*hp*pa*tc*tc*tf) /(2*dA*def*hp*pa*tc*tf) -(2*atk*pc*tc*tf
                    - Math.sqrt((4*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 8*Math.pow(atk,2)*pc
                    *pf*tc*Math.pow(tc,2)*tf + 4*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2)
                    - 4*atk*dA*def*hp*pa*pc*tc*Math.pow(tc,2)*Math.pow(tf,2) + 4*atk*dA*def*hp*pa*pc*tc*tc*Math.pow(tf,2)
                    - 4*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf + 4*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*tc*tf
                    + Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 2*Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*tc*Math.pow(tf,2)
                    + Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 32*dA*Math.pow(def,2)*Math.pow(hp,2)*pa*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 2*atk*pf*tc*tc
                    + dA*def*hp*pa*tc*tf - dA*def*hp*pa*tc*tc*tf)/(2*dA*def*hp*pa*tc*tf)));
        }
        //Region 12
        if (atkStage > -4 && atkStage < 0 && defStage <= -4) {
            length = (int)-((Math.sqrt(64*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 128*Math.pow(atk,2)*pc*pf*tc
                    *Math.pow(tc,2)*tf + 64*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2) - 16*atk*dA*def
                    *hp*pa*pc*tc*Math.pow(tc,2)*Math.pow(tf,2) + 16*atk*dA*def*hp*pa*pc*tc*tc*Math.pow(tf,2) - 16*atk*dA
                    *def*hp*pa*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf + 16*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*tc*tf
                    + Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 2*Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*tc*Math.pow(tf,2)
                    + Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 32*dA*Math.pow(def,2)*Math.pow(hp,2)*pa*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 8*atk*pc*tc*tf + 8*atk*pf*tc*tc
                    + dA*def*hp*pa*tc*tf - dA*def*hp*pa*tc*tc*tf)/(2*dA*def*hp*pa*tc*tf) -(8*atk*pc*tc*tf
                    - Math.sqrt(64*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 128*Math.pow(atk,2)*pc*pf*tc*Math.pow(tc,2)*tf
                    + 64*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2) - 16*atk*dA*def*hp*pa*pc*tc*Math.pow(tc,2)
                    *Math.pow(tf,2) + 16*atk*dA*def*hp*pa*pc*tc*tc*Math.pow(tf,2) - 16*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf
                    + 16*atk*dA*def*hp*pa*pf*Math.pow(tc,2)*tc*tf + Math.pow(dA,2)*Math.pow(def,2)*Math.pow(hp,2)
                    *Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 2*Math.pow(dA,2)*Math.pow(def,2)
                    *Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*tc*Math.pow(tf,2) + Math.pow(dA,2)*Math.pow(def,2)
                    *Math.pow(hp,2)*Math.pow(pa,2)*Math.pow(tc,2)*Math.pow(tf,2) + 32*dA*Math.pow(def,2)*Math.pow(hp,2)
                    *pa*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 8*atk*pf*tc*tc + dA*def*hp*pa*tc*tf - dA*def*hp*pa*tc*tc*tf)/(2*dA*def*hp*pa*tc*tf));
        }
        //Region 13
        if (atkStage <= -4 && defStage > 4) {
            length = (int)Math.floor((8*def*hp*tc*tf)/(double)(atk*pc*tf + atk*pf*tc));
        }
        //Region 14
        if (atkStage <= -4 && defStage >= 0 && defStage <= 4) {
            int length1 = (int)Math.floor((Math.sqrt(4*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 8*Math.pow(atk,2)*pc*pf*tc*Math.pow(tc,2)*tf
                    + 4*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2) + 4*atk*dB*def*hp*pb*pc*tc*Math.pow(tc,2)*Math.pow(tf,2)
                    - 4*atk*dB*def*hp*pb*pc*tc*tc*Math.pow(tf,2) + 4*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf
                    - 4*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*tc*tf + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)
                    *Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2) + 2*Math.pow(dB,2)*Math.pow(def,2)
                    *Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*tc*Math.pow(tf,2) + Math.pow(dB,2)*Math.pow(def,2)
                    *Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tf,2) - 32*dB*Math.pow(def,2)*Math.pow(hp,2)
                    *pb*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 2*atk*pc*tc*tf + 2*atk*pf*tc*tc - dB*def*hp*pb*tc*tf
                    + dB*def*hp*pb*tc*tc*tf)/(2*dB*def*hp*pb*tc*tf));
            int length2 = (int)((2*atk*pc*tc*tf - Math.sqrt(4*Math.pow(atk,2)*Math.pow(pc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 8*Math.pow(atk,2)*pc*pf*tc*Math.pow(tc,2)*tf + 4*Math.pow(atk,2)*Math.pow(pf,2)*Math.pow(tc,2)*Math.pow(tc,2)
                    + 4*atk*dB*def*hp*pb*pc*tc*Math.pow(tc,2)*Math.pow(tf,2) - 4*atk*dB*def*hp*pb*pc*tc*tc*Math.pow(tf,2)
                    + 4*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*Math.pow(tc,2)*tf - 4*atk*dB*def*hp*pb*pf*Math.pow(tc,2)*tc*tf
                    + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    + 2*Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*tc*Math.pow(tf,2)
                    + Math.pow(dB,2)*Math.pow(def,2)*Math.pow(hp,2)*Math.pow(pb,2)*Math.pow(tc,2)*Math.pow(tf,2)
                    - 32*dB*Math.pow(def,2)*Math.pow(hp,2)*pb*Math.pow(tc,2)*tc*Math.pow(tf,2)) + 2*atk*pf*tc*tc
                    - dB*def*hp*pb*tc*tf + dB*def*hp*pb*tc*tc*tf)/(2*dB*def*hp*pb*tc*tf));
            length = Math.min(length1, length2);
        }
        //Region 16
        if (atkStage <= -4 && defStage <= -4) {
            length = (int) Math.floor((2*def*hp*tc*tf)/(double)(atk*pc*tf + atk*pf*tc));
        }
        return length;
    }

    public static String[] stringToArray(String str) {
        String check = ", ";
        if (str.isEmpty()) {
            return new String[]{""};
        }
        else if (str.contains(check)) {
            return str.split(", ");
        }
        return new String[]{str};
    }
    public static String[] bracketedStringToArray(String str) {
        String noBrackets = str.substring(1, str.length() - 1);
        return noBrackets.split(", ");
    }

}
