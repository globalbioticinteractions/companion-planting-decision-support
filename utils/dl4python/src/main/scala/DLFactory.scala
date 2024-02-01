package nl.vu.kai.dl4python

import nl.vu.kai.dl4python.datatypes._

object DLFactory {
  def conceptAssertion(concept: Concept, individual: Individual) = {
    new ConceptAssertion(concept,individual)
  }

  def roleAssertion(role: Role, individual1: Individual, individual2: Individual) =
    new RoleAssertion(role, individual1, individual2)

  def conceptName(name: String) = ConceptName(name)

  def roleName(name: String) = RoleName(name)

  def individual(name: String) = Individual(name)
}
