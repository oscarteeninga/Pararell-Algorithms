package resolver

import model.Matrix

case class SequenceResolver(matrix: Matrix) {
  var cost: Int = Int.MaxValue
  var path: List[Int] = Nil

  def updateBest(cost: Int, path: List[Int]): Unit = {
    if (cost < this.cost) {
      this.cost = cost
      this.path = path
    }
  }

  def resolve: (List[Int], Int) = {
    val cities = matrix.columns.tail
    val beginCity = matrix.columns.head
    val (reduced, cost) = matrix.reduce

    def resolveCity(m: Matrix, cities: List[Int], city: Int, cost: Int, path: List[Int]): Unit = {
      if (cities.isEmpty) updateBest(cost + reduced.m((city, beginCity)), path ++ Set(beginCity))
      else {
        cities.foreach { nextCity =>
          val (newReduced, reduceCost) = m.eliminate((city, nextCity)).eliminate(city, nextCity).reduce
          resolveCity(newReduced, cities.diff(List(nextCity)), nextCity, cost + reduceCost + reduced.m((city, nextCity)), path ++ Set(nextCity))
        }
      }
    }

    resolveCity(reduced, cities.toList, beginCity, cost, List(beginCity))
    (path, this.cost)
  }
}
