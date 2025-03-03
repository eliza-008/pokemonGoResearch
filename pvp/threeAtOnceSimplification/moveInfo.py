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
print(fastMovesTable.prettify())