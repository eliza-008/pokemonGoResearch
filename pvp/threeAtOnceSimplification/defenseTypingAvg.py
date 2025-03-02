# Using the three pokemon at once simplification, this takes the type charts for each being attacked and averages them
import pandas as pd
import numpy as np

# define pokemon on team
team = ["Luxray", "Sylveon", "Greninja"]

# define type chart
typeDef = pd.read_csv("typeChart.csv").to_numpy()
typeDef = np.delete(typeDef, 0, axis=1) # remove first row 

typeDict = { # the number corresponsds to the row collumn
    'Normal': 0, 'Fire': 1, 'Water': 2, 'Electric': 3, 'Grass': 4, 'Ice': 5, 
    'Fighting': 6, 'Poison': 7, 'Ground': 8, 'Flying': 9, 'Psychic': 10, 
    'Bug': 11, 'Rock': 12, 'Ghost': 13, 'Dragon': 14, 'Dark': 15, 'Steel': 16, 
    'Fairy': 17, 'None': 18
}

# define pokemon types
pokeTypes = pd.read_csv("pokemonData.csv")


# create pokemon defense vector for each pokemon in team

# average all pokemon in team

# output