#################
# Before running the script, make sure that dl4python jar is running with:
# java -jar "./utils/dl4python-0.1.2-jar-with-dependencies.jar"
#################
import itertools

import pandas as pd
from py4j.java_gateway import JavaGateway

# create the links to the java gateway and parse the ontology

iri = 'http://www.semanticweb.org/kai/ontologies/2024/companion-planting#'
gateway = JavaGateway()
parser = gateway.getOWLParser(False)
onto = parser.parseFile('./owl/companion-planting-base0.1.owl')
fac = gateway.getDLFactory()

# load the companion planting dataset/table
df = pd.read_csv('./../datasets/companion-planting.csv', names=['v1', 'v2', 'rel'])

# adding the various vegetables
vegetables = pd.unique(df[['v1', 'v2']].values.ravel())
vegetableConcept = fac.getConceptName(iri + "Vegetable")

allVegConcepts = []
for v in vegetables:
    concept = fac.getConceptName(iri+ v.replace(" ", ""))
    allVegConcepts.append(concept)
    onto.addStatement(fac.getGCI(concept, vegetableConcept))

    # onto.addStatement(
    #     fac.getGCI(
    #         fac.getConjunction(
    #             fac.getExistentialRoleRestriction(
    #                 fac.getRole(iri + 'anticompanion_with'),
    #                 concept
    #             ),
    #             fac.getExistentialRoleRestriction(
    #                 fac.getRole(iri + 'idealNeighbour'),
    #                 concept
    #             )
    #         ),
    #         fac.getBottom()
    #
    #     )
    # )

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
for v1,v2 in itertools.combinations(allVegConcepts,2):
    onto.addStatement(fac.disjointnessAxiom(v1,v2))


# adding the companion/anticompanion restrictions
for _, row in df.iterrows():
    v1 = fac.getConceptName(iri + row['v1'].replace(" ", ""))
    v2 = fac.getConceptName(iri + row['v2'].replace(" ", ""))
    if row['rel'] == 'companion':
        #this might be redundant
        role = fac.getRole(iri + 'companion_with')
        onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))

    if row['rel'] == 'antagonistic':
        role = fac.getRole(iri + 'anticompanion_with')
        onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))

# export the ontology
gateway.getOWLExporter().exportOntology(onto, './owl/companion_planting-with-tablev3.owl')

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
