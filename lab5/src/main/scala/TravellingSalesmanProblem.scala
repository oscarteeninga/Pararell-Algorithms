import model.Matrix
import resolver.{NaiveResolver, SequenceResolver}

import scala.util.Random

object TravellingSalesmanProblem extends App {
  val m = (0 to 4).flatMap(i => (0 to 4).map(j => (i, j) -> Random.between(5, 10))).toMap

  println(NaiveResolver(Matrix(m)).resolve)
  println(SequenceResolver(Matrix(m)).resolve)
}
