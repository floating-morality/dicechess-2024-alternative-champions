package org.floatingmorality.dicechess.domain

final case class Tournament(
    id: BigInt,
    name: String,
    participants: Vector[Participant]
)

final case class Participant(
    name: String,
    points: Points
)

final case class Points(
    points: Double,
    buchholzCoef: Double,
    buchholzAdvancedCoef: Double
)

object Points:

  given PointsOrdering: Ordering[Points] = Ordering.by[Points, (Double, Double, Double)] { p =>
    (-p.points, -p.buchholzCoef, -p.buchholzAdvancedCoef)
  }
