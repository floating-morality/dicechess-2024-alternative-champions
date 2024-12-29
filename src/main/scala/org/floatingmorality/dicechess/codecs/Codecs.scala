package org.floatingmorality.dicechess.codecs

import org.floatingmorality.dicechess.domain.{Participant, Points, Tournament}
import io.circe.*
import io.circe.generic.semiauto.*

given ParticipantDecoder: Decoder[Participant] = (c: HCursor) =>
  for
    name                 <- c.get[String]("name")
    points               <- c.get[Double]("points")
    buchholzCoef         <- c.get[Double]("buchholzCoef")
    buchholzAdvancedCoef <- c.get[Double]("buchholzAdvancedCoef")
  yield Participant(name, Points(points, buchholzCoef, buchholzAdvancedCoef))

given TournamentDecoder: Decoder[Tournament] = deriveDecoder
