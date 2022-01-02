package sequential

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

  def resolve: List[Int] = {
    val cities = matrix.columns.tail
    val beginCity = matrix.columns.head
    val (reduced, cost) = matrix.reduce

    def resolveCity(m: Matrix, citiesLeft: List[Int], city: Int, cost: Int, path: List[Int]): Unit = {
      if (citiesLeft.isEmpty) {
        updateBest(cost + reduced.m((city, beginCity)), path ++ Set(beginCity))
      } else {
        citiesLeft.map { nextCity =>
          val (newReduced, reduceCost) = m.eliminate((city, nextCity)).eliminate(city, nextCity).reduce
          (nextCity, newReduced, reduceCost)
        }.filter(_._3 + cost < this.cost).sortBy(_._3).foreach {
          case (nextCity, newReduced, reduceCost) =>
            resolveCity(newReduced, citiesLeft.diff(List(nextCity)), nextCity, cost + reduceCost + reduced.m((city, nextCity)), path ++ Set(nextCity))
        }
      }
    }

    resolveCity(reduced, cities.toList, beginCity, cost, List(beginCity))
    path
  }
}
