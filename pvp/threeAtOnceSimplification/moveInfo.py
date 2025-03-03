# Generates csv of moves and their characteristics
# Maybe make a function eventually thatll just grab move data instead of having to manually do it every time?

import requests
from bs4 import BeautifulSoup
import csv

url = "https://bulbapedia.bulbagarden.net/wiki/List_of_moves_in_Pok%C3%A9mon_GO"
response = requests.get(url)
# print(response.status_code)   # If getting an error, run this and check response code
response.raise_for_status()     # Raise an error if request fails

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

# same thing for the charged moves table!

chargeMovesTable = None

# Locate the Charged Attacks table
for table in tempTable:
    h2 = table.find_previous("h2")
    if h2 and h2.find("span", id="Charged_Attacks"):
        chargeMovesTable = table
        break
# print(chargeMovesTable.prettify())
if chargeMovesTable:
    #fastMoves = [] # fastMoves is soley for debugging, gonna be written to csv later anyways
    rows = chargeMovesTable.find_all("tr")[1:] # skip header

    # open csv and prepare to write
    with open("movesData.csv", mode="a", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)

        for n in rows:
            columns = n.find_all("td")

            if columns:
                moveId     = columns[0].get_text(strip=True)
                moveName   = columns[1].get_text(strip=True)
                moveType   = columns[2].get_text(strip=True) 
                movePower  = columns[3].get_text(strip=True)  
                print(movePower) # error here idk why
                #moveCost   = columns[9].get_text(strip=True)  
                #moveStat   = columns[10].get_text(strip=True)  
                #moveChance = columns[11].get_text(strip=True)
                #fastMoves.append(moveName)
                #writer.writerow([moveName,moveType,"1","movePower","0","0","moveCost","moveStat","moveChance"])
