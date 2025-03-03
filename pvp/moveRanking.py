## this script currently only works for fast attacks, uses the idea of average power per turn to rank fast moves
## very prelim equation, only accounts for power and turns, with a questionable implementation of how energy boost effects it
## also maybe account for the risk of a move taking more turns

import pandas as pd
import numpy as np

 # Read the CSV file
df = pd.read_csv('movesData.csv')

# calculate ranking
df['Ranking'] = (df['Power'] / df['Turns']) + .5 * df['Energy Boost']/ df['Turns']

# Min-Max Normalization using NumPy
min_rank = df['Ranking'].min()
max_rank = df['Ranking'].max()
df['Normalized Ranking'] = (df['Ranking'] - min_rank) / (max_rank - min_rank)

# Get the top 10 moves
top_moves = df.sort_values(by='Normalized Ranking', ascending=False).head(10)
top_moves = top_moves[['Id', 'Name', 'Type', 'Power', 'Turns', 'Energy Boost', 'Ranking', 'Normalized Ranking']]


# Output the result
print(top_moves)

