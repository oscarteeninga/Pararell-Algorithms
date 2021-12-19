import model.Matrix

import scala.util.Random

object TravellingSalesmanProblem extends App {
  val m = (0 to 3).flatMap(i => (0 to 3).map(j => (i, j) -> Random.between(5, 10))).toMap

  println(Matrix(m))
  println()
  val (x, cost) = Matrix(m).reduce
  println(x)
  println()
  println(cost)
  println()
  println(x.eliminate(1, 3))
  println()
  println(x.eliminate((0, 0)))
}
