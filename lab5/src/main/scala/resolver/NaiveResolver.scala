package resolver

import model.Matrix

case class NaiveResolver(matrix: Matrix) {

  var cost: Int = Int.MaxValue
  var path: List[Int] = Nil

  def updateBest(cost: Int, path: List[Int]): Unit ={
    if (cost < this.cost) {
      this.cost = cost
      this.path = path
    }
  }

  def resolve: (List[Int], Int) = {
    val cities = matrix.columns.tail
    val beginCity = matrix.columns.head
    def resolveAcc(citiesLeft: List[Int], city: Int, cost: Int, path: List[Int]): Unit = {
      if (citiesLeft.isEmpty) updateBest(cost + matrix.m(city, beginCity), path ++ List(beginCity))
      else {
        citiesLeft.foreach { nextCity =>
          resolveAcc(citiesLeft.diff(List(nextCity)), nextCity, cost + matrix.m(city, nextCity), path ++  List(nextCity))
        }
      }
    }
    resolveAcc(cities.toList, beginCity, 0, List(beginCity))
    (path, cost)
  }
}
