package simulations

import java.util.Locale

import baseConf.BaseSimulation
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import models.JavaUser

import scala.concurrent.duration._


class SimulationWithEnvVariables extends BaseSimulation {


  var faker = new Faker(new Locale("en_us"))
  var objectMapper = new ObjectMapper()

  def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt



  def  getSpecificUser() = {
    exec(http("get specific user")
      .get("/users/1").check(status.in(200, 304)))
  }

  def getAllusers() = {
    exec(http("get all users")
      .get("/users").check(status.in(200, 304)))
  }

  def createUser() = {
    exec {
      val user = new JavaUser(faker.name().name(), faker.name().username(), faker.internet().emailAddress())
      http("Create User").post("/users")
        .body(StringBody(objectMapper.writeValueAsString(user)))
        .check(status.is(201), jsonPath("$.name").is(s"${user.getName}"))
    }
  }




  val scn = scenario("User Service Scenario")
    .repeat(3){
      exec(getSpecificUser())
    }
    .pause(1)
    .repeat(2){
      exec(getAllusers())
    }
    .pause(1)
    .repeat(1) {
      exec(createUser())
    }
    .pause(1)



  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration ${testDuration} seconds")
  }

  setUp(scn.inject(
    nothingFor(5 seconds),
    rampUsers(userCount) during(rampDuration)
  ).protocols(httpConf))
    .maxDuration(testDuration)


}
