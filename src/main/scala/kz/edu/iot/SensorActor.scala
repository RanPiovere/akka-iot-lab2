package kz.edu.iot

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import java.time.Instant

object SensorActor {
  sealed trait Command

  final case class SensorReading(
      sensorId: String,
      temperatureCelsius: Double,
      humidityPercent: Double,
      capturedAt: Instant = Instant.now()
  )

  final case class Record(reading: SensorReading, replyTo: ActorRef[RecordResult]) extends Command
  final case class GetLatest(replyTo: ActorRef[LatestResult]) extends Command

  sealed trait RecordResult
  final case class Accepted(reading: SensorReading) extends RecordResult
  final case class Rejected(reason: String) extends RecordResult

  sealed trait LatestResult
  final case class Latest(reading: Option[SensorReading]) extends LatestResult

  def apply(sensorId: String): Behavior[Command] = active(sensorId, None)

  private def active(sensorId: String, latest: Option[SensorReading]): Behavior[Command] =
    Behaviors.receiveMessage {
      case Record(reading, replyTo) if reading.sensorId != sensorId =>
        replyTo ! Rejected(s"Expected sensor '$sensorId', received '${reading.sensorId}'")
        Behaviors.same

      case Record(reading, replyTo) if !valid(reading) =>
        replyTo ! Rejected("Temperature or humidity is outside the accepted range")
        Behaviors.same

      case Record(reading, replyTo) =>
        replyTo ! Accepted(reading)
        active(sensorId, Some(reading))

      case GetLatest(replyTo) =>
        replyTo ! Latest(latest)
        Behaviors.same
    }

  private def valid(reading: SensorReading): Boolean =
    reading.temperatureCelsius >= -80.0 &&
      reading.temperatureCelsius <= 120.0 &&
      reading.humidityPercent >= 0.0 &&
      reading.humidityPercent <= 100.0
}
