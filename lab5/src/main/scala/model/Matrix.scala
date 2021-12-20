package model

import Matrix.M

case class Matrix(m: M) {
  def reduce: (Matrix, Int) = {
    val (is, js) = m.keySet.unzip
    var cost = 0
    val reducedRows = is.flatMap { i =>
      val (row, min) = reducedRow(m, i)
      cost += min
      row
    }.toMap
    val reducedRowsAndColumns = js.flatMap { j =>
      val (column, min) = reducedColumn(reducedRows, j)
      cost += min
      column
    }.toMap
    (Matrix(reducedRowsAndColumns), cost)
  }

  def rows: Set[Int] = m.keySet.map(_._1)
  def columns: Set[Int] = m.keySet.map(_._2)

  def reducedRow(matrix: M, row: Int): (M, Int) = {
    val r = matrix.filter(_._1._1 == row)
    if (r.values.forall(_ > 1000)) (r, 0)
    else {
      val min = r.values.min
      (r.view.mapValues(_ - min).toMap, min)
    }
  }

  def reducedColumn(matrix: M, column: Int): (M, Int) = {
    val c = matrix.filter(_._1._2 == column)
    if (c.values.forall(_ > 1000)) (c, 0)
    else {
      val min = c.values.min
      (c.view.mapValues(_ - min).toMap, min)
    }
  }

  def eliminate(row: Int, column: Int): Matrix = {
    val eliminated = m.map {
      case (key, _) if key._1 == row || key._2 == column => (key, Int.MaxValue)
      case v => v
    }
    Matrix(eliminated)
  }

  def eliminate(rc: (Int, Int)): Matrix = {
    Matrix(m.removed(rc) ++ Map(rc -> Int.MaxValue))
  }

  override def toString: String = {
    val (is, js) = m.keySet.unzip
    is.toList.sorted.map { i =>
      js.toList.sorted.map { j =>
        val v = m((i, j))
        if (v > 1000) "\u221e" else v.toString
      }.mkString("\t")
    }.mkString("\n")
  }
}

object Matrix {
  type M = Map[(Int, Int), Int]
}

