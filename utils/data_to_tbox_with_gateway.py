#################
# Before running the script, make sure that dl4python jar is running with:
# java -jar "./utils/dl4python-0.1.2-jar-with-dependencies.jar"
#################

import pandas as pd
from py4j.java_gateway import JavaGateway

# create the links to the java gateway and parse the ontology
gateway = JavaGateway()
parser = gateway.getOWLParser(False)
onto = parser.parseFile('./owl/companion-planting-base.owl')
el = gateway.getELFactory()

# load the companion planting dataset/table
df = pd.read_csv('./../datasets/companion-planting.csv', names=['v1', 'v2', 'rel'])

# adding the various vegetables
vegetables = pd.unique(df[['v1', 'v2']].values.ravel())
vegConcept = el.getConceptName("Vegetable")

for v in vegetables:
    concept = el.getConceptName(v.replace(" ", ""))

    onto.addStatement(el.getGCI(concept, vegConcept))

# adding the companion/anticompanion restrictions
for _, row in df.iterrows():
    v1 = el.getConceptName(row['v1'].replace(" ", ""))
    v2 = el.getConceptName(row['v2'].replace(" ", ""))
    if row['rel'] == 'companion':
        role = el.getRole('companion_with')
        onto.addStatement(el.getGCI(v1, el.getExistentialRoleRestriction(role, v2)))
    if row['rel'] == 'antagonistic':
        role = el.getRole('anticompanion_with')
        onto.addStatement(el.getGCI(v1, el.getExistentialRoleRestriction(role, v2)))

# export the ontology
gateway.getOWLExporter().exportOntology(onto, './owl/companion_planting-with-table.owl')

# converting the default iri into the correct one - will improve in the future
with open('./../owl/companion_planting-with-table.owl', 'r') as file:
    filedata = file.read()

filedata = filedata.replace('http://example.com/ns/foo#',
                            'http://www.semanticweb.org/kai/ontologies/2024/companion-planting#')

# Write the file out again
with open('./../owl/companion_planting-with-table.owl', 'w') as file:
    file.write(filedata)
