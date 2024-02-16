#! /usr/bin/python

from py4j.java_gateway import JavaGateway

gateway = JavaGateway()
factory = gateway.getDLFactory()

ontology = factory.getOntology()

class1 = factory.getConceptName("A")
class2 = factory.getConceptName("B")

ontology.addStatement(factory.getGCI(class1,class2))
ontology.addAnnotation(factory.getLabelAnnotation(class1, "Super cool class about A", "en"))
ontology.addAnnotation(factory.getLabelAnnotation(class1, "Super coole Klasse ueber A", "de"))
ontology.addAnnotation(factory.getLabelAnnotation(class1, "Class coolissimo d'A", "it"))

gateway.getOWLExporter().exportOntology(ontology,"test.owl")

