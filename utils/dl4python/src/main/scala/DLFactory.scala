package nl.vu.kai.dl4python

import nl.vu.kai.dl4python.datatypes._

object DLFactory {
  def getConceptName(name: String) =
    ConceptName(name)

  def getTop() =
    TopConcept

  def getConjunction(first: Concept, second: Concept) =
    ConceptConjunction(Seq(first,second))

  def getRole(name: String) =
    RoleName(name)

  def getExistentialRoleRestriction(role: Role, filler: Concept) =
    ExistentialRoleRestriction(role,filler)

  def getGCI(lhs: Concept, rhs: Concept) =
    GeneralConceptInclusion(lhs,rhs)

  def getBottom() = BottomConcept

  def conceptAssertion(concept: Concept, individual: Individual) = {
    new ConceptAssertion(concept,individual)
  }

  def roleAssertion(role: Role, individual1: Individual, individual2: Individual) =
    new RoleAssertion(role, individual1, individual2)

  def conceptName(name: String) = ConceptName(name)

  def roleName(name: String) = RoleName(name)

  def individual(name: String) = Individual(name)
}
