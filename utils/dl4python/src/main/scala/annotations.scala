package nl.vu.kai.dl4python.datatypes

trait Annotation
case class LabelAnnotation(name: Name, label: String, language: String) extends Annotation