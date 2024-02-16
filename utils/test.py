#! /usr/bin/python

from py4j.java_gateway import JavaGateway

gateway = JavaGateway()
factory = gateway.getDLFactory()


# Test label annotations

ontology = factory.getOntology()

class1 = factory.getConceptName("A")
class2 = factory.getConceptName("B")

ontology.addStatement(factory.getGCI(class1,class2))
ontology.addAnnotation(factory.getLabelAnnotation(class1, "Super cool class about A", "en"))
ontology.addAnnotation(factory.getLabelAnnotation(class1, "Super coole Klasse ueber A", "de"))
ontology.addAnnotation(factory.getLabelAnnotation(class1, "Class coolissimo d'A", "it"))

gateway.getOWLExporter().exportOntology(ontology,"test.owl")


# Test unsupported axioms

ontology = gateway.getOWLParser(False).parseFile("../../owl/companion-planting-base0.1.owl")

print(ontology)

gateway.getOWLExporter().exportOntology(ontology,"test2.owl")

