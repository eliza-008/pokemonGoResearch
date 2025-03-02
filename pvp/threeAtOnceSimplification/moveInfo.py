# generates csv of moves and their characteristics, maybe make a function eventually thatll just grab move data instead of having to manually do it every time?
import requests
from bs4 import BeautifulSoup
import csv

url = "https://bulbapedia.bulbagarden.net/wiki/List_of_moves_in_Pok%C3%A9mon_GO"
response = requests.get(url)
# print(response.status_code) # if getting an error, run this and check response code
response.raise_for_status()  # Raise an error if request fails

# filter data
soup = BeautifulSoup(response.text, "html.parser")
tempTable = soup.find("table", class_="roundy") # sortable roundy jquery-tablesorter is table class but only roundy works???
fastMovesTable = None

# Lcate the Fast Moves table
for table in tempTable:
    if "Fast Moves" in table.find_previous("title"):
        fastMovesTable = table
        break
print(fastMovesTable)

# 0 clue why not working LOL,