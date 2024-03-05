#################
# Before running the script, make sure that dl4python jar is running with:
# java -jar "./utils/dl4python/target/dl4python-0.1.5-jar-with-dependencies.jar"
#################
import itertools

import pandas as pd
from py4j.java_gateway import JavaGateway

def toPascalCase(s):
    return ''.join(x for x in s.title() if not (x.isspace() or x == '.'))

import numpy as np
# create the links to the java gateway and parse the ontology

iri = 'http://www.semanticweb.org/kai/ontologies/2024/companion-planting#'
gateway = JavaGateway()
parser = gateway.getOWLParser(False)
onto = parser.parseFile('./owl/companion-planting-base0.1.owl')
fac = gateway.getDLFactory()

# load the companion planting dataset/table
df = pd.read_csv('../datasets/Processed/companion_plants_including_taxon.csv')

#load the names-taxon-products dataframe: idx, taxon,plantCommonName,plantWikidata,productCommonName,productWikidata
ntp = pd.read_csv('../datasets/Processed/names-taxon-products.csv')

# adding the various plants
plants=pd.concat([df[['v1','taxon_v1']].rename(columns={'v1':'v','taxon_v1':'taxon'}),df[['v2','taxon_v2']].rename(columns={'v2':'v','taxon_v2':'taxon'})]).drop_duplicates().values
floraConcept = fac.getConceptName(iri + "Flora")

allPlantConcepts = []
for v in plants:
    if (not pd.isna(v[1])):
        concept = fac.getConceptName(iri+ toPascalCase(v[1])) #v1 for latin name
        allPlantConcepts.append(concept)
        onto.addStatement(fac.getGCI(concept, floraConcept))
        #common name
        onto.addAnnotation(fac.getLabelAnnotation(concept,v[0].title(),'en' ))

        #taxon name
        onto.addAnnotation(fac.getTaxonAnnotation(concept, v[1].title()))
        row = ntp[ntp.taxon == v[1]]

        # if there is a link to the wikidata entry
        if not row.empty:
            onto.addAnnotation(fac.getSeeAlsoAnnotation(concept, row.iloc[0].plantWikidata))

        #add neighbouring axioms
        # exist (companion_with some C) and (neighbour some C) SubClassOf (companionNeighbour some C)
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

        # exist (anticompanion_with some C) and (neighbour some C) SubClassOf (incompatibleNeighbour some C)
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

## add disjointness, each plant is disjoint with the others
for v1,v2 in itertools.combinations(allPlantConcepts,2):
    onto.addStatement(fac.disjointnessAxiom(v1,v2))


# adding the companion/anticompanion restrictions
for _, row in df.iterrows():
        if not (pd.isna(row.taxon_v1) or pd.isna(row.taxon_v2)):
            v1 = fac.getConceptName(iri + toPascalCase(row.taxon_v1))
            v2 = fac.getConceptName(iri + toPascalCase(row.taxon_v2))

            if row['rel'] == 'companion':
                #this might be redundant
                role = fac.getRole(iri + 'companion_with')
                onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))

            if row['rel'] == 'antagonistic':
                role = fac.getRole(iri + 'anticompanion_with')
                onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))

# export the ontology
gateway.getOWLExporter().exportOntology(onto, './owl/companion_planting-with-tablev4.owl')


