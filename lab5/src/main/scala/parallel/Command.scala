package parallel

sealed trait Command
case object Resolve
case class Resolved(cost: Int, path: List[Int])

