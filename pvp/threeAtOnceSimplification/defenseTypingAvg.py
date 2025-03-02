def defenseTypingAvg(team):
# explanation of how typing in pokemon works: each type is weak to moves from other types, either 2x 1x or 0.5x, where that is the damage multiplier
# if a pokemon is of two types, each of element multiplies eachother, so if a pokemon is water and ice (Lapras) and water type multiplier to fire is x0.5 
# and the ice multiplier to fire is x2.0, so lapras takes 0.5*2 = 1x damage from fire 
# Using the three pokemon at once simplification, this takes the type charts for each being attacked and averages them
    import pandas as pd
    import numpy as np

    # define pokemon on team
    

    # define type chart
    typeDef = pd.read_csv("typeChart.csv").to_numpy()
    typeDef = np.delete(typeDef, 0, axis=1) # remove first row 

    typeDict = { # the number corresponsds to the row collumn, wondering if this is even needed?? 
        # added it to make code cleaner but i think its just more messy, later problem
        'Normal': 0, 'Fire': 1, 'Water': 2, 'Electric': 3, 'Grass': 4, 'Ice': 5, 
        'Fighting': 6, 'Poison': 7, 'Ground': 8, 'Flying': 9, 'Psychic': 10, 
        'Bug': 11, 'Rock': 12, 'Ghost': 13, 'Dragon': 14, 'Dark': 15, 'Steel': 16, 
        'Fairy': 17, 'None': 18
    }

    defenseVector = np.zeros((len(team), typeDef.shape[0])) 

    # define pokemon types, pokemon refers to dataset, poke refers to single member
    pokemonTypesData = pd.read_csv("pokemonData.csv")
    pokemonNames = pokemonTypesData.iloc[:, 0] # all the names
    pokemonTypes = pokemonTypesData.iloc[:, 1] # all the types
    for n in range(len(team)):
        teamIndex = pokemonNames[pokemonNames.str.contains(team[n])].index # index vector of team 
        teamType = pokemonTypes.iloc[teamIndex[0]].split(",") # cleans data from string to vector [type 1, type 2]
        teamType = [m.strip() for m in teamType] # removes spaces

        
        tempTypeIndex = [typeDict[m] for m in teamType]
        if len(team) > 1:
            defenseVector[n, :] = np.prod(typeDef[:, tempTypeIndex], axis=1)
        else:
            defenseVector[n, :] = typeDef[:, tempTypeIndex[0]]

            
    # two approaches right now, no idea which one is better
    # approach 1: average all pokemon in team 
    avgDefenseVector = np.mean(defenseVector, axis=0)
    # approach 2: multiply all together, this one seems to exaggerate weakness and strengths while other is more moderate
    #avgDefenseVector2 = np.prod(defenseVector, axis=0)
    #print(avgDefenseVector2)
    # output
    return avgDefenseVector