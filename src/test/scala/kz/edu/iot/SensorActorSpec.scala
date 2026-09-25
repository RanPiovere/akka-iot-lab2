package kz.edu.iot

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.Instant

class SensorActorSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import SensorActor._

  "SensorActor" should {
    "accept a valid reading and return it as the latest value" in {
      val actor = spawn(SensorActor("sensor-1"))
      val recordProbe = createTestProbe[RecordResult]()
      val latestProbe = createTestProbe[LatestResult]()
      val reading = SensorReading("sensor-1", 22.5, 48.0, Instant.parse("2026-01-01T00:00:00Z"))

      actor ! Record(reading, recordProbe.ref)
      recordProbe.expectMessage(Accepted(reading))

      actor ! GetLatest(latestProbe.ref)
      latestProbe.expectMessage(Latest(Some(reading)))
    }

    "reject readings with impossible humidity" in {
      val actor = spawn(SensorActor("sensor-2"))
      val probe = createTestProbe[RecordResult]()

      actor ! Record(SensorReading("sensor-2", 20.0, 140.0), probe.ref)

      probe.expectMessageType[Rejected]
    }

    "reject readings sent to the wrong sensor actor" in {
      val actor = spawn(SensorActor("sensor-3"))
      val probe = createTestProbe[RecordResult]()

      actor ! Record(SensorReading("sensor-other", 19.0, 40.0), probe.ref)

      probe.expectMessageType[Rejected]
    }
  }
}
