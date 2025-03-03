# Generates csv of moves and their characteristics
# Maybe make a function eventually thatll just grab move data instead of having to manually do it every time?

import requests
from bs4 import BeautifulSoup
import csv

url = "https://bulbapedia.bulbagarden.net/wiki/List_of_moves_in_Pok%C3%A9mon_GO"
response = requests.get(url)
# print(response.status_code)                           # If getting an error, run this and check response code
response.raise_for_status()                             # Raise an error if request fails

soup = BeautifulSoup(response.text, "html.parser")      # Filter data
tempTable = soup.find_all("table", class_="roundy")     # Get all tables from URL
fastMovesTable = None

# Locate the Charged Attacks table
for table in tempTable:
    h2 = table.find_previous("h2")
    if h2 and h2.find("span", id="Charged_Attacks"):
        chargedMovesTable = table
    
# Extract rows from the table
rows = chargedMovesTable.find_all("tr")

# Look at each row (skip header row)
for row in rows[1:]:                                          # Skipping the header
    
    # Get each column from the row and ignore graphics (they mess up what columns train power/energy cost + stats are in)
    columns = [td for td in row.find_all("td") if "min-height" and "background-color" not in td.get("style", "")] 


    if len(columns) >= 2:                                     # Need at least the move and its ID number
        moveName = columns[1].get_text(strip=True)            # Move name (2nd column)
        moveID = columns[0].get_text(strip=True)              # ID number (1st column)

        gymPower = columns[3].get_text(strip=True)            # Power (Gym/Raid) (4th column)
        gymEnergyCost = columns[4].get_text(strip=True)       # Energy cost (Gym/Raid) (5th column)
            
        trainerPower = columns[8].get_text(strip=True)        # Power (Trainer) (9th column)
        trainerEnergyCost = columns[9].get_text(strip=True)   # Energy cost (Trainer) (10th column)

        statModifier = columns[10].get_text(strip=True)       # Stat modifier (11th column)
        statChance = columns[11].get_text(strip=True)         # Stat chance (12th column)
        
        if moveName and moveID:                               # Check to make sure they're not empty (some values are)

            # Reformat for CSV, but print out results for now
            print(f"""Move: {moveName},
                      ID: {moveID}, 
                      Power (Gym/Raid): {gymPower}, 
                      Energy (Gym/Raid): {gymEnergyCost}, 
                      Power (vs Trainer): {trainerPower}, 
                      Energy (vs Trainer): {trainerEnergyCost},
                      Stat Modifier: {statModifier},
                      Stat Chance: {statChance}""")

# Use this to look at the table in HTML format
# print(chargedMovesTable.prettify())
