package kz.edu.iot

import akka.actor.typed.ActorSystem
import com.typesafe.config.ConfigFactory

object IotServiceApp {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val profile = sys.props.getOrElse("iot.profile", config.getString("iot.profile")).toLowerCase
    val profileConfig = config.getConfig(s"iot.profiles.$profile")
    val description = profileConfig.getString("description")

    val system = ActorSystem(SensorActor("demo-sensor"), "akka-iot-service", config)
    system.log.info("Akka IoT service started with profile {}: {}", profile, description)
  }
}
