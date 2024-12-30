package org.floatingmorality.dicechess

import cats.effect.{IO, IOApp}
import cats.syntax.eq.*
import io.circe.parser.decode
import org.floatingmorality.dicechess.codecs.TournamentDecoder
import org.floatingmorality.dicechess.domain.*
import org.floatingmorality.dicechess.domain.Points.PointsOrdering

import scala.collection.immutable.ListMap
import scala.io.Source
import scala.util.Using

object Main extends IOApp.Simple:

  override def run: IO[Unit] =
    for
      tournament1 <- readResource("5_0_dice_chess_championship_2024.json")
      tournament2 <- readResource("3_0_dice_chess_championship_2024.json")
      tournament3 <- readResource("5_5_dice_chess_championship_2024.json")
      leaders = calculateLeadersByPoints(tournament1, tournament2, tournament3)
      _ <- IO.println(generateMarkdownTable(leaders))
    yield ()

  private def readResource(resource: String) = IO {
    Using.resource(Source.fromResource(resource))(_.mkString)
  }.flatMap { content =>
    IO.fromEither(decode[Tournament](content))
  }

  private def calculateLeadersByPlaces(tournaments: Tournament*): ListMap[String, Int] =
    val allPlayers = tournaments.flatMap(_.participants.map(_.name)).distinct

    val playerToTotalPlaces = allPlayers.map { player =>
      val totalPlaces = tournaments.map { tournament =>
        val participants = tournament.participants
        participants.indexWhere(_.name === player) match {
          case -1    => Math.ceil(participants.size / 2.0).toInt
          case index => index + 1
        }
      }.sum
      player -> totalPlaces
    }.toMap

    ListMap.from(playerToTotalPlaces.toSeq.sortBy(_._2))

  private def calculateLeadersByPoints(tournaments: Tournament*) =
    val playerToPoints = tournaments
      .flatMap(_.participants)
      .groupMapReduce(_.name)(_.points) { (p1, p2) =>
        Points(
          points               = p1.points + p2.points,
          buchholzCoef         = p1.buchholzCoef + p2.buchholzCoef,
          buchholzAdvancedCoef = p1.buchholzAdvancedCoef + p2.buchholzAdvancedCoef
        )
      }

    ListMap.from(playerToPoints.toSeq.sortBy(_._2))

  private def generateMarkdownTable(pointsMap: Map[String, Points]): String = {
    val header = "| Place | Player                | Points | Buchholz Coef | Buchholz Advanced Coef |\n" +
      "|-------|-----------------------|--------|---------------|-------------------------|"
    val rows = pointsMap.toSeq.zipWithIndex
      .map { case ((player, points), index) =>
        f"| ${index + 1}%-5d | ${player.padTo(21, ' ')} | ${points.points}%-6.1f | ${points.buchholzCoef}%-13.1f | ${points.buchholzAdvancedCoef}%-23.1f |"
      }
      .mkString("\n")
    header + "\n" + rows
  }

  private def generateMarkdownTable2(placementsMap: ListMap[String, Int]): String = {
    val header = "| Place | Player                | Sum of Places |\n" +
      "|-------|-----------------------|-----------------|"
    val rows = placementsMap.toSeq.zipWithIndex
      .map { case ((player, totalPlacement), index) =>
        f"| ${index + 1}%-5d | ${player.padTo(21, ' ')} | ${totalPlacement}%-15d |"
      }
      .mkString("\n")
    header + "\n" + rows
  }
