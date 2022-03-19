package parallel

import akka.util.Timeout
import model.Matrix

import java.util.concurrent.{Executors, Semaphore}
import scala.concurrent.{Await, ExecutionContext, Future, blocking}
import scala.concurrent.duration.{Duration, SECONDS}

case class ParallelResolver(matrix: Matrix, n: Int) {

  implicit val timeout: Timeout = new Timeout(100, SECONDS)

  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(n))

  var cost: Int = Int.MaxValue
  var path: List[Int] = Nil

  val semaphore: Semaphore = new Semaphore(n)

  def updateBest(cost: Int, path: List[Int]): Unit = this.synchronized {
    if (cost < this.cost) {
      this.cost = cost
      this.path = path
    }
  }

  def resolve: List[Int] = {
    val cities = matrix.columns.tail
    val beginCity = matrix.columns.head
    val (reduced, cost) = matrix.reduce

    def resolveCity(m: Matrix, citiesLeft: List[Int], city: Int, cost: Int, path: List[Int]): Unit = {
      if (citiesLeft.isEmpty) {
        updateBest(cost + reduced.m((city, beginCity)), path ++ Set(beginCity))
      } else citiesLeft.map { nextCity =>
        val (newReduced, reduceCost) = m.eliminate((city, nextCity)).eliminate(city, nextCity).reduce
        (nextCity, newReduced, reduceCost)
      }.filter(_._3 + cost < this.cost).sortBy(_._3).collect {
        case (nextCity, newReduced, reduceCost) =>
          //          semaphore.acquire()
          Future {
            resolveCity(
              newReduced,
              citiesLeft.diff(List(nextCity)),
              nextCity, cost + reduceCost + reduced.m((city, nextCity)),
              path ++ Set(nextCity)
            )
          }
        //          semaphore.release()
      }
    }

    resolveCity(reduced, cities.toList, beginCity, cost, List(beginCity))
    path
  }
  //
  //
  //  override def receive: Receive = {
  //    case Resolve =>
  //      if (citiesLeft.isEmpty) sender() ! Resolved(cost + matrix.m(city, beginCity), path ++ List(beginCity))
  //      else citiesLeft.foreach { nextCity =>
  //        val (newReduced, reduceCost) = matrix.eliminate((city, nextCity)).eliminate(city, nextCity).reduce
  //        if (reduceCost + cost < getBest) {
  //          val newResolver = context.actorOf(
  //            Props(
  //              CityResolverActor(
  //                newReduced,
  //                reduced,
  //                citiesLeft.diff(List(nextCity)),
  //                nextCity,
  //                cost + reduceCost + reduced.m((city, nextCity)),
  //                path ++ Set(nextCity),
  //                beginCity
  //              )
  //            )
  //          )
  //          (newResolver ? Resolve).onComplete {
  //            result => result.get
  //
  //          }
  //        }
  //      }
  //      context.stop(self)
  //  }
}
