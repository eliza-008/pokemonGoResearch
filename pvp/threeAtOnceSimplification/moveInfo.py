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

# Locate the Fast Attacks table
for table in tempTable:
    h2 = table.find_previous("h2")
    if h2 and h2.find("span", id="Fast_Attacks"):
        fastMovesTable = table
        break
# print(fastMovesTable.prettify())


if fastMovesTable:
    #fastMoves = [] # fastMoves is soley for debugging, gonna be written to csv later anyways
    rows = fastMovesTable.find_all("tr")[1:] # skip header

    # open csv and prepare to write
    with open("movesData.csv", mode="w", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        writer.writerow(["Id","Name", "Type","isCharged","Power","Energy Boost","Turns","Energy Cost","Stat Mod","Chance"])

        for n in rows:
            columns = n.find_all("td")
            if columns:
                moveId    = columns[0].get_text(strip=True)
                moveName  = columns[1].get_text(strip=True)  
                moveType  = columns[2].get_text(strip=True)  
                movePower = columns[8].get_text(strip=True)  
                moveBoost = columns[9].get_text(strip=True)  
                moveTurns = columns[10].get_text(strip=True)  

                #fastMoves.append(moveName)
                writer.writerow([moveId,moveName,moveType,"0",movePower,moveBoost,moveTurns,"0","0,0,0,0,0,0","0"])

# Locate the Charged Attacks table
for table in tempTable:
    h2 = table.find_previous("h2")
    if h2 and h2.find("span", id="Charged_Attacks"):
        chargedMovesTable = table
    
# Extract rows from the table
rows = chargedMovesTable.find_all("tr")

# Open file and write header
with open("movesData.csv", mode="a", newline="", encoding="utf-8") as file:
    writer = csv.writer(file)

    # Look at each row (skip header row)
    for row in rows[1:]:  # Skipping the header
        columns = [td for td in row.find_all("td") if "min-height" and "background-color" not in td.get("style", "")] 

        if len(columns) >= 2:
            moveName = columns[1].get_text(strip=True)
            moveID = columns[0].get_text(strip=True)
            moveType = columns[2].get_text(strip=True)
            gymPower = columns[3].get_text(strip=True)
            gymEnergyCost = columns[4].get_text(strip=True)
            trainerPower = columns[8].get_text(strip=True)
            trainerEnergyCost = columns[9].get_text(strip=True)
            statModifier = columns[10].get_text(strip=True)
            statChance = columns[11].get_text(strip=True)
            statChance = statChance.replace("%","")
            try:
                statChance = float(statChance) / 100       # Convert to float and divide by 100
            except ValueError:
                statChance = ""              

            if moveName and moveID:
                # print(f"""Move: {moveName},
                #           ID: {moveID}, 
                #           Type: {moveType},
                #           Power (Gym/Raid): {gymPower}, 
                #           Energy (Gym/Raid): {gymEnergyCost}, 
                #           Power (vs Trainer): {trainerPower}, 
                #           Energy (vs Trainer): {trainerEnergyCost},
                #           Stat Modifier: {statModifier},
                #           Stat Chance: {statChance}""")
    
                # Writing to file inside the `with open(...)` block
                writer.writerow([moveID, moveName, moveType, "1", trainerPower, "0", "0", trainerEnergyCost, statModifier, statChance])

# Use this to look at the table in HTML format
# print(chargedMovesTable.prettify())
