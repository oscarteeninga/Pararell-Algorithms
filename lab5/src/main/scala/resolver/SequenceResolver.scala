package resolver

import model.Matrix

case class SequenceResolver(matrix: Matrix, cost: Int) {
  def resolve: (Set[Int], Int) = {
    val cities = matrix.columns
    //TODO
    (Set.empty, 0)
  }
}
