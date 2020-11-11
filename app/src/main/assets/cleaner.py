import pandas as pd
#pd.set_option("display.max_rows", 10, "display.max_columns", None, 'display.max_colwidth', None)
pd.set_option("display.max_rows", 10, "display.max_columns", None, 'display.max_colwidth', None, 'display.width', None)

dim = pd.read_csv("Dimensions.csv", header=None)
del dim[0]; del dim[1]
dim.columns = ['Painting ID', 'Dimensions']

loc = pd.read_csv("Locations.csv", header=None)
del loc[1]; del loc[4]
loc.columns = ['Painting ID', 'Location', 'Artist', 'Title']

#comb = pd.DataFrame(data={'Painting ID':dim['Painting ID'], 'Dimensions':dim['Dimensions']})
combination = {}
for i in range(len(loc['Painting ID'])):
    if (loc['Painting ID'][i] in list(dim['Painting ID'])):
        combination[loc['Painting ID'][i]] = [loc['Location'][i], loc['Artist'][i], loc['Title'][i]]
    else:
        id_deconcat = loc['Painting ID'][i].split(' ')[0]
        id_deconcat = id_deconcat.split('.')
        for m in range(len(id_deconcat)-1, -1,-1):
            new_ID = id_deconcat[0]
            for j in range(1,m+1):
                new_ID = new_ID + f".{id_deconcat[j]}"
            if (new_ID in list(dim['Painting ID'])):
                combination[new_ID] = [loc['Location'][i], loc['Artist'][i], loc['Title'][i]]
                break
            if (m==0):
                quit(f"ERROR - PAINTING ID {loc['Painting ID'][i]} NOT FOUND")
    
for item in combination.keys():
    for i in range(len(list(dim['Painting ID']))):
        if (dim['Painting ID'][i] == item):
            combination[item].append(dim['Dimensions'][i])
            #print(combination[item])

painting_IDs = list(combination.keys())
locations = []
artist = []
title = []
dimensions = []

for item in painting_IDs:
    locations.append(combination[item][0])
    artist.append(combination[item][1])
    title.append(combination[item][2])
    dimensions.append(combination[item][3])
comb = pd.DataFrame(data={'Painting ID':painting_IDs,'Location':locations,'Artist':artist,'Title':title,'Dimension':dimensions})
comb.insert(2,"Location Type",None)
comb.insert(3,"Rack",None)
comb.insert(6,"Height",None)
comb.insert(7,"Width",None)
comb.insert(8,"Depth",None)
comb

def dimens(dim_string):
    h = None; w = None; d = None
    try:
        dim_string = dim_string.split("\n")[0]
    except AttributeError:
        return (None, None, None)
    if ("cm)" not in dim_string):
        pass
        if ("dimensions variable" in dim_string):
            return (h,w,d)
        if ('inches' in dim_string):
            dim_string = dim_string[0:dim_string.find("inches")]
            if ('x' in dim_string):
                val = dim_string.split('x')
                #print(val)
                val[0] = val[0].strip()
                val[1] = val[1].strip()
                if ('5/8' in val[0]):
                    h = float(val[0].split(' ')[0]) + (5/8)
                if ('5/8' in val[1]):
                    w = float(val[1].split(' ')[0]) + (5/8)
                if ('¾' in val[0]):
                    h = float(val[0].split(' ')[0]) + (3/4)
                if ('¾' in val[1]):
                    h = float(val[1].split(' ')[0]) + (3/4)
                if ('¼' in val[0]):
                    h = float(val[0].split(' ')[0]) + (1/4)
                if ('¼' in val[1]):
                    h = float(val[1].split(' ')[0]) + (1/4)
                return (h,w,d)
            if ('×' in dim_string):
                val = dim_string.split('×')
                #print(val)
                val[0] = val[0].strip()
                val[1] = val[1].strip()
                if ('5/8' in val[0]):
                    h = float(val[0].split(' ')[0]) + (5/8)
                if ('5/8' in val[1]):
                    w = float(val[1].split(' ')[0]) + (5/8)
                if ('¾' in val[0]):
                    h = float(val[0].split(' ')[0]) + (3/4)
                if ('¾' in val[1]):
                    h = float(val[1].split(' ')[0]) + (3/4)
                if ('¼' in val[0]):
                    h = float(val[0].split(' ')[0]) + (1/4)
                if ('¼' in val[1]):
                    h = float(val[1].split(' ')[0]) + (1/4)
                return (h,w,d)
        #print(dim_string)
    else:
        if ("x" in dim_string.split("cm)")[0].split("(")[len(dim_string.split("cm)")[0].split("("))-1]):
            pass
            val = dim_string.split("cm)")[0].split("(")[len(dim_string.split("cm)")[0].split("("))-1]
            comps = val.split("x")
            h = round(float(comps[0].strip())/2.54,2)
            w = round(float(comps[1].strip())/2.54,2)
            if (len(comps)>2):
                d = round(float(comps[2].strip())/2.54,2)
        elif ("×" in dim_string.split("cm)")[0].split("(")[len(dim_string.split("cm)")[0].split("("))-1]):
            pass
            val = dim_string.split("cm)")[0].split("(")[len(dim_string.split("cm)")[0].split("("))-1]
            comps = val.split("×")
            h = round(float(comps[0].strip())/2.54,2)
            w = round(float(comps[1].strip())/2.54,2)
            if (len(comps)>2):
                d = round(float(comps[2].strip())/2.54,2)
        else:
            h = round(float(dim_string.split("cm)")[0].split("(")[len(dim_string.split("cm)")[0].split("("))-1].strip())/2.54,2)
            pass
            return (h,None,None)
        #print(dim_string)
    return (h,w,d)
    
for i in range(len(comb['Location'])):
    storageloc = comb['Location'][i].split(',')
    try:
        if ('SCREEN' in storageloc[2].strip().upper()):
            comb['Location Type'][i] = storageloc[2].strip().title()
        else:
            #print('\n',storageloc, comb['Painting ID'][i],'\n')
            pass
    except IndexError as E:
        '''print(storageloc, comb['Painting ID'][i])
        print(E)'''
        pass
    
    try:
        if(not ' ' in storageloc[3].strip()):
            comb['Rack'][i] = storageloc[3]
            
    except IndexError as E:
        pass
    
    (height, width, depth) = dimens(comb['Dimension'][i])
    comb['Height'][i] = height
    comb['Width'][i] = width
    comb['Depth'][i] = depth
comb

def finder(id):
    for i in range(len(comb['Painting ID'])):
        if (comb['Painting ID'][i] == id):
            return (comb['Painting ID'][i],comb['Location Type'][i],comb['Rack'][i],comb['Artist'][i],comb['Title'][i],comb['Height'][i],comb['Width'][i])
    return None
#finder("1969.10.1")
for i in range(len(comb['Artist'])):
    try:
        #print(comb['Artist'][i].replace('\n',' '))
        comb['Artist'][i] = comb['Artist'][i].replace('\n',' ')
        #print(comb['Title'][i].replace('\n',' '))
        comb['Title'][i] = comb['Title'][i].replace('\n',' ')
        #print(comb['Dimension'][i].replace('\n',' '))
        #comb['Dimension'][i] = comb['Dimension'][i].replace('\n',' ')
        
    except AttributeError:
        pass

pass

'''for item in comb['Dimension']:
    print(item, end='\n\n\n')'''
#del(comb[0])
del comb['Dimension']

comb.to_csv("nasher_clean_info.csv", index= False)
#comb
