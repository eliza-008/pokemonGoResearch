# -*- coding: utf-8 -*-
"""
Created on Mon Apr 19 13:52:11 2021

@author: daxat
"""
import math, xlwt, pandas as pd
from xlwt import Workbook

class Pokemon:
    
  def __init__(self, number, name, poke_type, max_CP, stamina, attack, defense):
    self.poke_type = poke_type
    self.name = name
    self.number = number
    self.max_CP = max_CP
    self.stamina = stamina
    self.attack = attack
    self.defense = defense
    self.fast_moves = []
    self.charged_moves = []

    
  def __str__(self):
    prnt =  "Number: %s, Pokemon: %s, Type: %s, Max CP: %d, Stamina: %d, Attack: %d, Defense: %.2f \n"  % \
      (self.number, self.name, str(self.poke_type), self.max_CP, self.stamina, self.attack, self.defense)
    prnt += "Fast Moves:"
    for item in self.fast_moves:
        prnt += "\n\t" + item.name
    prnt += "\nCharged Moves:"
    for item in self.charged_moves:
        prnt += "\n\t" + item.name
    return prnt


class ChargedMove:

	def __init__(self, move_type, name, power, power_pvp, energy, energy_pvp, cast_time):
		self.move_type = move_type
		self.name = name
		self.power = power
		self.power_pvp = power_pvp
		self.energy = energy
		self.energy_pvp = energy_pvp
		self.cast_time = cast_time

	def __str__(self):
		return "Move Type: %s, Move Name: %s, Power: %d, Power PVP: %d, Energy: %d, Energy PVP: %d, Cast Time: %.2f" % \
			(self.move_type, self.name, self.power, self.power_pvp, self.energy, self.energy_pvp, self.cast_time)


class FastMove(ChargedMove):

	def __init__(self, move_type, name, power, power_pvp, energy, energy_pvp, cast_time, turns):
		ChargedMove.__init__(self, move_type, name, power, power_pvp, energy, energy_pvp, cast_time)
		self.turns = turns

	def __str__(self):
		return "Move Type: %s, Move Name: %s, Power: %d, Power PVP: %d, Energy: %d, Energy PVP: %d, Cast Time: %.2f, Turns: %d" % \
			(self.move_type, self.name, self.power, self.power_pvp, self.energy, self.energy_pvp, self.cast_time, self.turns)


def STAB(move, pokemon):
  if move.move_type in pokemon.poke_type:
    multiplier = 1.2
  else:
    multiplier = 1
  return multiplier


WD = {
  "Clear":["Gra","Gro","Fir"],
  "Cloud":["Fig","Poi","Fai"],
  "Snow":["Ice","Ste"],
  "Wind":["Dra","Psy","Fly"],
  "Part_Cloud":["Nor","ROc"],
  "Rain":["Wat","ELe","Bug"],
  "Fog":["Gho","Dar"]
}

def weather(move,w):
    if move.move_type in WD[w]:
      boost = 1.2
    else:
      boost = 1
    return boost


bonus_table = {"Nor" : {"Nor" : 1, "Fig" : 1, "Fly" : 1, "Poi" : 1, "Gro" : 1, 
                          "Roc" : 0.625, "Bug" : 1, "Gho" : 0.391, "Ste" : 0.625, 
                          "Fi" : 1, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 1, 
                          "Ice" : 1, "Dra" : 1, "Dar" : 1, "Fai" : 1},

                "Fig" : {"Nor" : 1.6, "Fig" : 1, "Fly" : 0.625, "Poi" : 0.625, "Gro" : 1,
                          "Roc" : 1.6, "Bug" : 0.625, "Gho" : 0.391, "Ste" : 1.6,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 0.625,
                          "Ice" : 1.6, "Dra" : 1, "Dar" : 1.6, "Fai" : 0.625},

               "Fly" : {"Nor" : 1, "Fig" : 1.6, "Fly" : 1, "Poi" : 1, "Gro" : 1,
                          "Roc" : 0.625, "Bug" : 1.6, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1.6, "Ele" : 0.625, "Psy" : 1,
                          "Ice" : 1, "Dra" : 1, "Dar" : 1, "Fai" : 1},

               "Poi" : {"Nor" : 1, "Fig" : 1, "Fly" : 1, "Poi" : 0.625, "Gro" : 0.625,
                          "Roc" : 0.625, "Bug" : 1, "Gho" : 0.625, "Ste" : 0.391,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1.6, "Ele" : 1, "Psy" : 1,
                          "Ice" : 1, "Dra" : 1, "Dar" : 1, "Fai" : 1.6},

               "Gro" : {"Nor" : 1, "Fig" : 1, "Fly" : 0.391, "Poi" : 1.6, "Gro" : 1,
                          "Roc" : 1.6, "Bug" : 0.625, "Gho" : 1, "Ste" : 1.6,
                          "Fir" : 1.6, "Wat" : 1, "Gra" : 0.625, "Ele" : 1.6, "Psy" : 1,
                          "Ice" : 1, "Dra" : 1, "Dar" : 1, "Fai" : 1},

               "Roc" : {"Nor" : 1, "Fig" : 0.625, "Fly" : 1.6, "Poi" : 1, "Gro" : 0.625,
                          "Roc" : 1, "Bug" : 1.6, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 1.6, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 1,
                          "Ice" : 1, "Dra" : 1, "Dar" : 1, "Fai" : 1},

               "Bug" : {"Nor" : 1, "Fig" : 0.625, "Fly" : 0.625, "Poi" : 0.625, "Gro" : 1,
                          "Roc" : 1, "Bug" : 1, "Gho" : 0.625, "Ste" : 0.625,
                          "Fir" : 0.625, "Wat" : 1, "Gra" : 1.6, "Ele" : 1, "Psy" : 1.6,
                          "Ice" : 1, "Dra" : 1, "Dar" : 1.6, "Fai" : 0.625},

               "Gho" : {"Nor" : 0.391, "Fig" : 1, "Fly" : 1, "Poi" : 1, "Gro" : 1,
                          "Roc" : 0.625, "Bug" : 1, "Gho" : 1.6, "Ste" : 0.625,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 1.6,
                          "Ice" : 1, "Dra" : 1, "Dar" : 0.625, "Fai" : 1},

               "Ste" : {"Nor" : 1, "Fig" : 1, "Fly" : 1, "Poi" : 1, "Gro" : 1,
                          "Roc" : 1.6, "Bug" : 1, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 0.625, "Wat" : 0.625, "Gra" : 1, "Ele" : 0.625, "Psy" : 1,
                          "Ice" : 1.6, "Dra" : 1, "Dar" : 1, "Fai" : 1.6},

               "Fir" : {"Nor" : 1, "Fig" : 1, "Fly" : 1, "Poi" : 1, "Gro" : 1,
                          "Roc" : 0.625, "Bug" : 1.6, "Gho" : 1, "Ste" : 1.6,
                          "Fir" : 0.625, "Wat" : 0.625, "Gra" : 1.6, "Ele" : 1, "Psy" : 1,
                          "Ice" : 1.6, "Dra" : 0.625, "Dar" : 1, "Fai" : 1},

               "Wat" : {"Nor" : 1, "Fig" : 1, "Fly" : 1, "Poi" : 1, "Gro" : 1.6,
                          "Roc" : 1.6, "Bug" : 1, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 1.6, "Wat" : 0.625, "Gra" : 0.625, "Ele" : 1, "Psy" : 1,
                          "Ice" : 1, "Dra" : 0.625, "Dar" : 1, "Fai" : 1},

               "Gra" : {"Nor" : 1, "Fig" : 1, "Fly" : 0.625, "Poi" : 0.625, "Gro" : 1.6,
                          "Roc" : 1.6, "Bug" : 0.625, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 0.625, "Wat" : 0.625, "Gra" : 1.6, "Ele" : 0.625, "Psy" : 1,
                          "Ice" : 1, "Dra" : 0.625, "Dar" : 1, "Fai" : 1},

               "Ele" : {"Nor" : 1, "Fig" : 1, "Fly" : 1.6, "Poi" : 1, "Gro" : 0.391,
                          "Roc" : 0.625, "Bug" : 1, "Gho" : 1, "Ste" : 1,
                          "Fir" : 1, "Wat" : 1.6, "Gra" : 0.625, "Ele" : 0.625, "Psy" : 1,
                          "Ice" : 1, "Dra" : 0.625, "Dar" : 1, "Fai" : 1},

               "Psy" : {"Nor" : 1, "Fig" : 1.6, "Fly" : 1, "Poi" : 1.6, "Gro" : 1,
                          "Roc" : 1, "Bug" : 1, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 0.625,
                          "Ice" : 1, "Dra" : 1, "Dar" : 0.391, "Fai" : 1},

               "Ice" : {"Nor" : 1, "Fig" : 1, "Fly" : 1.6, "Poi" : 1, "Gro" : 1.6,
                          "Roc" : 1, "Bug" : 1, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 0.625, "Wat" : 0.625, "Gra" : 1.6, "Ele" : 1, "Psy" : 1,
                          "Ice" : 0.625, "Dra" : 1.6, "Dar" : 1, "Fai" : 1},

               "Dra" : {"Nor" : 1, "Fig" : 1, "Fly" : 1, "Poi" : 1, "Gro" : 1,
                          "Roc" : 1, "Bug" : 1, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 1,
                          "Ice" : 1, "Dra" : 1.6, "Dar" : 1, "Fai" : 0.391},

               "Dar" : {"Nor" : 1, "Fig" : 0.625, "Fly" : 1, "Poi" : 1, "Gro" : 1.6,
                          "Roc" : 1, "Bug" : 1, "Gho" : 1.6, "Ste" : 1,
                          "Fir" : 1, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 1.6,
                          "Ice" : 1, "Dra" : 1, "Dar" : 0.625, "Fai" : 0.625},

               "Fai" : {"Nor" : 1, "Fig" : 1.6, "Fly" : 1, "Poi" : 0.625, "Gro" : 1,
                          "Roc" : 0.625, "Bug" : 1, "Gho" : 1, "Ste" : 0.625,
                          "Fir" : 0.625, "Wat" : 1, "Gra" : 1, "Ele" : 1, "Psy" : 1,
                          "Ice" : 1, "Dra" : 1.6, "Dar" : 1.6, "Fai" : 1}}

def type_bonus(move, poke):
  #This function takes two arguments, a move type and a pokemon.
  multiplier = 1
  for p_type in poke.poke_type:
    multiplier *= bonus_table[move.move_type][p_type]
  return multiplier


def FillCM():

	df = pd.read_csv("MoveList_ChargeMoves.csv")

	for index, row in df.iterrows():
		cm_obj = ChargedMove(row['type'], row['move'], row['power'], row['powerPvP'], row['energy'], row['energyPvP'], row['castTime'])
		#print(cm_obj)
		if cm_obj.name in CM.keys():
			print(cm_obj.name)
		CM[cm_obj.name] = cm_obj
        
"""	for key in CM.keys():
		print(CM[key])
"""


def FillFM():
    
	df2 = pd.read_csv("MoveList_FastMoves.csv")

	for index, row in df2.iterrows():
		fm_obj = FastMove(row['type'], row['move'], row['power'], row['powerPvP'], row['energy'], row['energyPvP'], row['castTime'], row['turns'])
		#print(fm_obj)
		if fm_obj.name in FM.keys():
			print(fm_obj.name)
		FM[fm_obj.name] = fm_obj
        
"""	for key in FM.keys():
		print(FM[key])
"""


def FillPD():
    
    df = pd.read_csv("Pokemon_Stats.csv")
    df2 = pd.read_csv("MoveSets_of_all_pokemon.csv")

    for index, row in df.iterrows():
        poke_type=[]
        poke_type.append(row["Type1"])
        if not pd.isna(row["Type2"]):
            poke_type.append(row["Type2"])
        p_obj = Pokemon(row['Num'], row['Pokemon'], poke_type[:], row['MaxCP'], row['Stamina'], row['Attack'], row['Defense'])
        if p_obj.name in PD.keys():
            print(p_obj.name)
        PD[p_obj.name] = p_obj

    for index, row in df2.iterrows():
        for item in row["FastMoves"][2:-2].split("', '"):
          PD[row["Name"]].fast_moves.append(FM[item.strip()])
        for item in row["ChargedMoves"][2:-2].split("', '"):
          PD[row["Name"]].charged_moves.append(CM[item.strip()])
          
"""    for key in PD.keys():
        print(PD[key])
"""

def mega_boost(move,meg):
    if meg == "":
        return 1
    elif move.move_type in PD[meg].poke_type:
        return 1.3
    else:
        return 1.1

def dps_move(move, att_p, def_p, att_l, def_l, w, meg):
   return (math.floor((att_p.attack * att_l / (2*def_p.defense * def_l)) * move.power * STAB(move, att_p) * weather(move, w) * type_bonus(move, def_p)) * mega_boost(move,meg) +1) / move.cast_time

PD = {}
FM = {}
CM = {}

def main():
    FillFM()
    FillCM()
    FillPD()
    
    megas = ["Venusaur Mega Venusaur", ""]
#    w = "Clear" #This is hardcoded at first for testing
    r = "Lugia" #raid boss name (string) is hardcoded at first for testing
#    a = "Pikachu" pokemon name (string) is hardcoded at first for testing
    rb = PD[r]
    att_l = 0.8402999 #level of attacker 
    rb_l = 1 #This needs to be corrected to represent Raid Boss level
    Best = {}
    num_of_bests = 6
    for w, mega, fmrb, cmrb in [[a,b,c,d] for a in WD.keys() for b in megas for c in rb.fast_moves for d in rb.charged_moves]:
        best_scores = [(0,"","","")]*num_of_bests
        for a in PD.keys():
            ap = PD[a]
            best_poke_score = (0,"","","")
            for fma, cma in [[x,y] for x in ap.fast_moves for y in ap.charged_moves]:
#    fmrb = rb.fast_moves[0] #This is hardcoded for testing
#    cmrb = rb.charged_moves[0] #Hard coded just for testing
#    fma = ap.fast_moves[0]
#    cma = ap.charged_moves[0]
                dps_raidBoss_fast = dps_move(fmrb, rb, ap, rb_l, att_l,w, mega)
                dps_raidBoss_charge = dps_move(cmrb, rb, ap, rb_l, att_l,w,mega)
                dps_raidBoss_delta = dps_raidBoss_charge - dps_raidBoss_fast
                if cmrb.energy != 100:
                    x_rb = (cmrb.energy - (rb.stamina/180.0) * cmrb.cast_time)/(fmrb.energy + (rb.stamina/180.0) * fmrb.cast_time)
                else:
                    x_rb = math.ceil((cmrb.energy - (rb.stamina/180.0) * cmrb.cast_time)/(fmrb.energy + (rb.stamina/180.0) * fmrb.cast_time))
                mu_raid = cmrb.cast_time / ((x_rb * fmrb.cast_time) + cmrb.cast_time)
                dps_rb = dps_raidBoss_fast + mu_raid * dps_raidBoss_delta
                dps_challenger_fast = dps_move(fma, ap, rb, att_l, rb_l, w,mega)
                dps_challenger_charge = dps_move(cma, ap, rb, att_l, rb_l, w,mega)
                dps_challenger_delta = dps_challenger_charge - dps_challenger_fast
                if cma.energy != 100:
                    x_a = (cma.energy - (dps_rb / 2.0) * cma.cast_time)/(fma.energy + (dps_rb / 2.0) * fma.cast_time)
                else:
                    x_a = math.ceil((cma.energy - (dps_rb / 2.0) * cma.cast_time)/(fma.energy + (dps_rb / 2.0) * fma.cast_time))    
                mu_a = cma.cast_time / ((x_a * fma.cast_time) + cma.cast_time)
                dps_a = dps_challenger_fast + mu_a * dps_challenger_delta
                dps_a *= ap.stamina*2/dps_rb / (ap.stamina*2/dps_rb + 15/6.0)
                if dps_a > best_poke_score[0]:
                    best_poke_score = (dps_a,a,fma.name,cma.name)
            if best_poke_score[0] > best_scores[0][0]:
                best_scores[0] = best_poke_score
            best_scores = sorted(best_scores, key = lambda x: x[0])
#        best_scores.reverse()
#        dps_list = []
        best_set = set()
        for item in best_scores:
            best_set.add((item[1],item[2],item[3]))
#            dps_less_list.append((item[1],item[2],item[3]))
#        Best[(w,mega,fmrb.name,cmrb.name)] = dps_less_list[:]
        best_scores = sorted(best_scores, key= lambda x: x[1] + x[2]+ x[3])
        Best[(w,mega,fmrb.name,cmrb.name)] = (best_set,best_scores[:])
    sames = []
    for key in Best.keys():
        new = True
        for same in sames:
            if Best[same[0]][0] == Best[key][0]:
                same.append(key)
                new = False
                break
        if new:
            sames.append([key])
    wb = Workbook()
    i=0
    for same in sames:
        sheet = wb.add_sheet("Sheet " + str(i))
        j = 1
        sheet.write(0,0,"Scenario #")
        sheet.write(0,1,"Weather")
        sheet.write(0,2,"Friendly Mega")
        sheet.write(0,3,"Boss Fast Move")
        sheet.write(0,4,"Boss Charged Move")
        k = 1
        for key in same:
            sheet.write(j,0,str(k))
            sheet.write(j,1,key[0])
            sheet.write(j,2,key[1])
            sheet.write(j,3,key[2])
            sheet.write(j,4,key[3])
            j += 1
            k += 1
#            print("Weather: %s, Mega: %s, Raid Boss Fast Move: %s, Raid Boss Charged move: %s\n"
#              % (key[0],key[1],key[2],key[3]))
        j += 1
        sheet.write(j,0,"Pokemon")
        sheet.write(j,1,"Fast Move")
        sheet.write(j,2,"Charged Move")
        for l in range(len(same)):
            sheet.write(j,l+3,"Scenario " + str(l+1) + " DPS")
        for p in range(num_of_bests):
#            print("\tPokemon: %s, Fast Move: %s, Charged Move: %s\n"
#                  % (item[0],item[1],item[2]))
            j += 1
            sheet.write(j,0,Best[same[0]][1][p][1])
            sheet.write(j,1,Best[same[0]][1][p][2])
            sheet.write(j,2,Best[same[0]][1][p][3])
            for l in range(len(same)):
                sheet.write(j,l+3,Best[same[l]][1][p][0])
        i += 1
    wb.save("Best_Raid_Counters.xls")
main()
