import re

def camelCase(s):
    parts = s.split('_')
    out = ''
    for i in range(len(parts)):
        if i ==0:
            out += parts[i]
        else:
            out += str.title(parts[i])
    return out


#print(re.sub("#[^_<>]+(?:_[^_<>]+)+>", lambda x: camelCase(x.group()), "http://www.semanticweb.org/kai/ontologies/2024/companion-planting#anticompanion_with>"))
with open('./../owl/companion-planting-base0.1.owl','r') as fp:
    with open('./../owl/companion-planting-base0.2.owl','w') as fout:
        for line in fp.readlines():
            fout.write(re.sub("#[^_<>]+(?:_[^_<>]+)+>", lambda x: camelCase(x.group()),
                   line)) #match a pattern that has #string1_string2_...>, and change it to camelcase


