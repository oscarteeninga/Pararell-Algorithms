import model.Matrix
import parallel.ParallelResolver
import sequential.{NaiveResolver, SequenceResolver}

import scala.util.Random

object TravellingSalesmanProblem extends App {
  val m = (0 to 6).flatMap(i => (0 to 6).map(j => (i, j) -> Random.between(0, 10000))).toMap

  var start = System.nanoTime()
  println("Native:\t\t" + NaiveResolver(Matrix(m)).resolve + " Time:\t" + (System.nanoTime() - start) + " ns")
  start = System.nanoTime()
  println("Sequential:\t" + SequenceResolver(Matrix(m)).resolve + " Time:\t" + (System.nanoTime() - start) + " ns")
  start = System.nanoTime()
  println("Parallel:\t" + ParallelResolver(Matrix(m), 16).resolve + " Time:\t" + (System.nanoTime() - start) + " ns")
}
