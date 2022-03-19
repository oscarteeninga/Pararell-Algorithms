import model.Matrix
import parallel.ParallelResolver
import sequential.{NaiveResolver, SequenceResolver}

import scala.util.Random

object TravellingSalesmanProblem extends App {
  val m = (0 to 9).flatMap(i => (0 to 9).map(j => (i, j) -> Random.between(0, 10000))).toMap

    var start = System.nanoTime()
//    println("Native:\t\t" + NaiveResolver(Matrix(m)).resolve + " Time:\t" + (System.nanoTime() - start) + " ns")
//    start = System.nanoTime()
//    println("Sequential:\t" + SequenceResolver(Matrix(m)).resolve + " Time:\t" + (System.nanoTime() - start) + " ns")
//    start = System.nanoTime()
    val resolver = ParallelResolver(Matrix(m), 16)
    println("Parallel:\t" + resolver.resolve + " Time:\t" + (System.nanoTime() - start) + " ns")

  (1 to 10000).foreach {
    _ =>
      Thread.sleep(1000)
      println(resolver.path)
  }
//  (4 to 16).foreach { n =>
//    val start = System.nanoTime()
//    (0 to 10).foreach(_ => ParallelResolver(Matrix(m), n).resolve)
//    println(n + "\t" + (System.nanoTime() - start) / 10 / 1000)
//  }
}
