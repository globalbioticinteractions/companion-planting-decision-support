import fitz
from collections import defaultdict
import csv

FILE = 'companion-planting-chart.pdf'
doc = fitz.open(FILE)
y_name = defaultdict(dict)
x_name = defaultdict(dict)
marks = set()

mid = lambda v1, v2: round((v1 + v2)/10) 
for page in doc.pages():
    # Get smile and X marks
    d = page.get_text("dict")
    blocks = d["blocks"]
    for block in blocks:
        if "lines" in block.keys():
            spans = block['lines']
            for span in spans:
                data = span['spans']
                for lines in data:
                    if '�' in lines['text'].lower():
                        x1, y1, x2, y2 = lines['bbox']
                        x, y = mid(x1, x2), mid(y1, y2)
                        # Smiles are in Wingdings, Xs are Arial
                        is_smile = (lines['font'] == 'Wingdings')
                        marks.add( (x,y,is_smile) )

    # Get plant names
    wlist = page.get_text("words")
    for x1, y1, x2, y2, w, *_ in wlist:
        if y1>150 and len(w) > 1 and int(x2) <= 94:
            y_name[mid(y1, y2)][x2] = w
            
        if 100 <= round(x2) <= 927 and 100 <= round(y2) <= 148:
            x_name[mid(x1, x2)][y2] = w
                        
y_names = [' '.join(name_words.values()) for name_words in y_name.values()]
x_names = [' '.join(name_words.values()) for name_words in x_name.values()]

xs, ys, _ = zip(*marks)
x_i = {x:i for i,x in enumerate(sorted(set(xs)))}
y_i = {y:i for i,y in enumerate(sorted(set(ys)))}

data = []
for x, y, is_smile in marks:
    data.append( (x_names[x_i[x]], y_names[y_i[y]], 'companion' if is_smile else 'antagonistic') )

with open('companion-planting.csv', 'w') as fw:
    csv.writer(fw).writerows(sorted(data))




