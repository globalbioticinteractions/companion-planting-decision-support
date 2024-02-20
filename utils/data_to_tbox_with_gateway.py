#################
# Before running the script, make sure that dl4python jar is running with:
# java -jar "./utils/dl4python-0.1.2-jar-with-dependencies.jar"
#################
import itertools

import pandas as pd
from py4j.java_gateway import JavaGateway

def toPascalCase(s):
    return ''.join(x for x in s.title() if not x.isspace())

import numpy as np
# create the links to the java gateway and parse the ontology

iri = 'http://www.semanticweb.org/kai/ontologies/2024/companion-planting#'
gateway = JavaGateway()
parser = gateway.getOWLParser(False)
onto = parser.parseFile('./owl/companion-planting-base-noPlantHierarchy.owl')
fac = gateway.getDLFactory()

# load the companion planting dataset/table
df = pd.read_csv('./../datasets/companion_plants_including_taxon.csv')
#load the names-taxon-products dataframe: idx, taxon,plantCommonName,plantWikidata,productCommonName,productWikidata
ntp = pd.read_csv('./../datasets/names-taxon-products.csv')

# adding the various vegetables
#vegetables = pd.unique(df[['v1', 'v2']].values.ravel())
plants=pd.concat([df[['v1','taxon_v1']].rename(columns={'v1':'v','taxon_v1':'taxon'}),df[['v2','taxon_v2']].rename(columns={'v2':'v','taxon_v2':'taxon'})]).drop_duplicates().values
floraConcept = fac.getConceptName(iri + "Flora")

allPlantConcepts = []
for v in plants:
    concept = fac.getConceptName(iri+ toPascalCase(v[0])) #v1 for latin name
    allPlantConcepts.append(concept)
    onto.addStatement(fac.getGCI(concept, floraConcept))
    onto.addAnnotation(fac.getLabelAnnotation(concept,v[0].title(),'en' ))
    if(not pd.isna(v[1])):
        onto.addAnnotation(fac.getLabelAnnotation(concept, v[1].title(), 'lt'))
        row = ntp[ntp.taxon == v[1]]
        if not row.empty:
            onto.addAnnotation(fac.getSeeAlsoAnnotation(concept, row.iloc[0].plantWikidata))

    onto.addStatement(
        fac.getGCI(
            fac.getConjunction(
                fac.getExistentialRoleRestriction(
                    fac.getRole(iri + 'neighbour'),
                    concept
                ),
                fac.getExistentialRoleRestriction(
                    fac.getRole(iri + 'companion_with'),
                    concept
                )
            ),
            fac.getExistentialRoleRestriction(
                fac.getRole(iri + 'companionNeighbour'),
                concept
            )
        )
    )

    onto.addStatement(
        fac.getGCI(
            fac.getConjunction(
                fac.getExistentialRoleRestriction(
                    fac.getRole(iri + 'neighbour'),
                    concept
                ),
                fac.getExistentialRoleRestriction(
                    fac.getRole(iri + 'anticompanion_with'),
                    concept
                )
            ),
            fac.getExistentialRoleRestriction(
                fac.getRole(iri + 'incompatibleNeighbour'),
                concept
            )
        )
    )

## add disjointness
for v1,v2 in itertools.combinations(allPlantConcepts,2):
    onto.addStatement(fac.disjointnessAxiom(v1,v2))


# adding the companion/anticompanion restrictions
for _, row in df.iterrows():
    v1 = fac.getConceptName(iri + toPascalCase(row.v1))
    v2 = fac.getConceptName(iri + toPascalCase(row.v2))
    if row['rel'] == 'companion':
        #this might be redundant
        role = fac.getRole(iri + 'companion_with')
        onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))

    if row['rel'] == 'antagonistic':
        role = fac.getRole(iri + 'anticompanion_with')
        onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))

# export the ontology

#current fix, remember to make neighbour symmetric
gateway.getOWLExporter().exportOntology(onto, './owl/companion_planting-with-tablev4.owl')

# converting the default iri into the correct one - will improve in the future
# with open('./../owl/companion_planting-with-table.owl', 'r') as file:
#     filedata = file.read()
#
# filedata = filedata.replace('http://example.com/ns/foo#',
#                             'http://www.semanticweb.org/kai/ontologies/2024/companion-planting#')
#
# # Write the file out again
# with open('./../owl/companion_planting-with-table.owl', 'w') as file:
#     file.write(filedata)
