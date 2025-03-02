# this script grabs info from db.pokemongohub.net, of all pokemon and their types
import requests
from bs4 import BeautifulSoup
import csv

# for loop evnetually
# open page

url = "https://pokemondb.net/go/pokedex?"
response = requests.get(url)
# print(response.status_code) # if getting an error, run this and check response code
response.raise_for_status()  # Raise an error if request fails

# filter data
soup = BeautifulSoup(response.text, "html.parser")
table = soup.find("table", class_="data-table")
# extract pokemon names and types
pokemon_data = []

# read table
for row in table.find_all("tr")[1:]:
    columns = row.find_all("td")
    if len(columns) > 2:  
        # pokemon name is in the second column 
        name_tag = columns[1].find("a")
        name = name_tag.text.strip() if name_tag else columns[1].text.strip()
        
        # pokemon types are in the third column
        types = [t.text.strip() for t in columns[2].find_all("a")]
        
        pokemon_data.append([name, types])
# write to csv
with open("pokemonData.csv", mode="w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file)
    
    # header row
    writer.writerow(["Name", "Type"])
    
    # actual data
    for p in pokemon_data:
        writer.writerow([p[0], ", ".join(p[1])])  # Join types with a comma

# print pokemon to verify works
for p in pokemon_data[:10]:
    print(p)