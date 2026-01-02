import java.util.ArrayList;

public class Pokemon {
    //Every Pokemon has corresponding Move objects for their movesets stored in ArrayLists
    //Those ArrayLists are used in main() to iterate through all moveset combinations
    public final String name;
    public String type1 = "", type2 = "";
    public final int id, stamina, attack, defense;
    public final ArrayList<Move> fastMoves = new ArrayList<>();
    public final ArrayList<Move> chargedMoves = new ArrayList<>();
    String[] types = {"Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel",
            "Fire", "Water", "Grass", "Electric", "Psychic", "Ice", "Dragon", "Dark", "Fairy"};

    //Simplified constructor to make objects of unavailable Pokemon for comparing/removing
    public Pokemon(String n, int i, String t1, String t2) {
        id = i;
        name = n;
        type1 = t1;
        type2 = t2;
        stamina = 0;
        attack = 0;
        defense = 0;
    }

    public Pokemon(String n, int i, String t1, String t2, int s, int a, int d, String[] fm, String[] cm,
                   ArrayList<Move> moves) {
        name = n;
        id = i;
        type1 = t1;
        type2 = t2;
        stamina = s;
        attack = a;
        defense = d;
        setFastMoves(fm, moves);
        setChargedMoves(cm, moves);
    }

    //Assign Move objects according to the moves the Pokemon can learn
    private void setFastMoves(String[] fm, ArrayList<Move> moves) {
        for (String str : fm) {
            for (Move move : moves) {
                str = removeTags(str);
                if (move.name.equals(str) && !fastMoves.contains(move)) {
                    fastMoves.add(move);
                }
            }
        }
    }

    private void setChargedMoves(String[] cm, ArrayList<Move> moves) {
        for (String str : cm) {
            //Locate the move being added
            for (Move move : moves) {
                str = removeTags(str);
                if (move.name.equals(str)) {
                    //Account for moves with different types like Weather Ball or Techno Blast
                    String type = "";
                    if (str.contains(" - ")) {
                        String[] splitName = str.split(" - ");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < splitName.length - 1; i++) {
                            stringBuilder.append(splitName[i]);
                        }
                        str = stringBuilder.toString();
                        type = splitName[splitName.length - 1];
                        if (type.equals(move.type)) {
                            chargedMoves.add(move);
                        }
                    }
                    //Update the moveset in all other cases
                    else {chargedMoves.add(move);}
                }
            }
        }
    }

    private String removeTags(String str) {
        //Alters the name to exclude the tag in parentheses at the end
        if (str.contains("(")) {
            String[] splitName = str.split(" ");
            StringBuilder strBuilder = new StringBuilder();
            if (str.contains("Community Day")) {
                for (int i = 0; i < splitName.length - 2; i++) {
                    strBuilder.append(splitName[i]);
                }
            }
            else for (int i = 0; i < splitName.length - 1; i++) {
                strBuilder.append(splitName[i]);
            }
            str = strBuilder.toString();
        }
        return str;
    }

    //for printing to output
    public void printInfo() {
        System.out.println(name + " ID #: " + id + " Type 1: " + type1 + " Type 2: " + type2 + " Stam: " + stamina
                + " Atk: " + attack + " Def: " + defense);
        /*System.out.print(" Fast moves: ");
        printFast();
        System.out.print("Charged moves: ");
        printCharged();
        System.out.println();*/
    }

    public String getInfo() {
        String info;
        if (!type2.equals("null")) {
            info = name + " #" + id + " " + type1 + " " + type2 + " Stam: " + stamina + " Atk: "
                    + attack + " Def: " + defense;
        }
        else {
            info = name + " #" + id + " " + type1 + " Stam: " + stamina + " Atk: "
                    + attack + " Def: " + defense;
        }
        return info;
    }

    public void printFast() {
        for (Move move : fastMoves) {
            System.out.print(move.name + ", ");
        }
    }

    public void printCharged() {
        for (int i = 0; i < chargedMoves.size() - 1; i++) {
            System.out.print(chargedMoves.get(i).name + ", ");
        }
        System.out.print(chargedMoves.getLast().name);
    }
}
