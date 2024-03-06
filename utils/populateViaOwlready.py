#################
# Before running the script, make sure that dl4python jar is running with:
# java -jar "./utils/dl4python/target/dl4python-0.1.5-jar-with-dependencies.jar"
#################
import itertools

import pandas as pd
from py4j.java_gateway import JavaGateway
from owlready2 import *
import types

def toPascalCase(s):
    return ''.join(x for x in s.title() if not (x.isspace() or x == '.'))

import numpy as np
# create the links to the java gateway and parse the ontology

iri = 'http://www.semanticweb.org/kai/ontologies/2024/companion-planting#'

onto = get_ontology('../owl/companion-planting-base0.2.rdf').load()
darwin = get_ontology('http://rs.tdwg.org/dwc/terms/')

with darwin:
    class scientificName(AnnotationProperty):
        pass
onto.imported_ontologies.append(darwin)
# load the companion planting dataset/table
df = pd.read_csv('../datasets/Processed/companion_plants_including_taxon.csv')

#load the names-taxon-products dataframe: idx, taxon,plantCommonName,plantWikidata,productCommonName,productWikidata
ntp = pd.read_csv('../datasets/Processed/names-taxon-products.csv')

# adding the various plants
plants=pd.concat([df[['v1','taxon_v1']].rename(columns={'v1':'v','taxon_v1':'taxon'}),df[['v2','taxon_v2']].rename(columns={'v2':'v','taxon_v2':'taxon'})]).drop_duplicates().values

with onto:


    Flora = types.new_class("Flora", (Thing,))
    # potatoClass = types.new_class("PotatoButInLatin", (Flora,))
    allPlantConcepts = dict()
    for p in plants:
        if (not pd.isna(p[1])):
            plant = types.new_class(toPascalCase(p[1]), (Flora,)) #class and IRI
            allPlantConcepts[p[1]] = plant
            plant.label = [locstr(p[0].title(), lang="en")] #english label
            #plant. = [p[1].title()] #find how to add custom annotations
           # darwin.scientificName
            plant.scientificName = [p[1].title()]
            row = ntp[ntp.taxon == p[1]]
            if not row.empty:
                plant.seeAlso = [row.iloc[0].plantWikidata]

            #neighbouring axioms
            gca = GeneralClassAxiom(onto.companionWith.some(plant) &
                                    onto.neighbour.some(plant)) #lhs
            gca.is_a.append(onto.companionNeighbour.some(plant))

            gca = GeneralClassAxiom(onto.anticompanionWith.some(plant) &
                                    onto.neighbour.some(plant))  # lhs
            gca.is_a.append(onto.incompatibleNeighbour.some(plant))

    AllDisjoint(list(allPlantConcepts.values()))

    for _, row in df.iterrows():
        if not (pd.isna(row.taxon_v1) or pd.isna(row.taxon_v2)):
            v1 = allPlantConcepts[row.taxon_v1]
            v2 = allPlantConcepts[row.taxon_v2]

            if row['rel'] == 'companion':
                if len(v1.companionWith) == 0 :
                    v1.companionWith = [v2]

                else:
                    v1.companionWith.append(v2)
            if row['rel'] == 'antagonistic':
                if len(v1.anticompanionWith) == 0:
                    v1.anticompanionWith = [v2]

                else:
                    v1.anticompanionWith.append(v2)

    onto.save(file='../owl/companion_planting_v5.owl')
'''
########################
'''

#
# allPlantConcepts = []
# for v in plants:
#
#
#     #add neighbouring axioms
#     # exist (companion_with some C) and (neighbour some C) SubClassOf (companionNeighbour some C)
#     onto.addStatement(
#         fac.getGCI(
#             fac.getConjunction(
#                 fac.getExistentialRoleRestriction(
#                     fac.getRole(iri + 'neighbour'),
#                     concept
#                 ),
#                 fac.getExistentialRoleRestriction(
#                     fac.getRole(iri + 'companion_with'),
#                     concept
#                 )
#             ),
#             fac.getExistentialRoleRestriction(
#                 fac.getRole(iri + 'companionNeighbour'),
#                 concept
#             )
#         )
#     )
#
#     # exist (anticompanion_with some C) and (neighbour some C) SubClassOf (incompatibleNeighbour some C)
#     onto.addStatement(
#         fac.getGCI(
#             fac.getConjunction(
#                 fac.getExistentialRoleRestriction(
#                     fac.getRole(iri + 'neighbour'),
#                     concept
#                 ),
#                 fac.getExistentialRoleRestriction(
#                     fac.getRole(iri + 'anticompanion_with'),
#                     concept
#                 )
#             ),
#             fac.getExistentialRoleRestriction(
#                 fac.getRole(iri + 'incompatibleNeighbour'),
#                 concept
#             )
#         )
#     )
#
# ## add disjointness, each plant is disjoint with the others
# for v1,v2 in itertools.combinations(allPlantConcepts,2):
#     onto.addStatement(fac.disjointnessAxiom(v1,v2))
#
#
# # adding the companion/anticompanion restrictions
# for _, row in df.iterrows():
#         if not (pd.isna(row.taxon_v1) or pd.isna(row.taxon_v2)):
#             v1 = fac.getConceptName(iri + toPascalCase(row.taxon_v1))
#             v2 = fac.getConceptName(iri + toPascalCase(row.taxon_v1))
#
#             if row['rel'] == 'companion':
#                 #this might be redundant
#                 role = fac.getRole(iri + 'companion_with')
#                 onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))
#
#             if row['rel'] == 'antagonistic':
#                 role = fac.getRole(iri + 'anticompanion_with')
#                 onto.addStatement(fac.getGCI(v1, fac.getExistentialRoleRestriction(role, v2)))
#
# # export the ontology
# gateway.getOWLExporter().exportOntology(onto, './owl/companion_planting-with-tablev4.owl')
#
#
