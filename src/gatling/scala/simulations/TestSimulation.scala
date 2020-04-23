package simulations

import java.util.Locale

import baseConf.BaseSimulation
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import models.JavaUser

import scala.concurrent.duration._

class TestSimulation extends BaseSimulation {

  var faker = new Faker(new Locale("en_us"))
  var objectMapper = new ObjectMapper()

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


  /*setUp(scn.inject(
    nothingFor(4 seconds),
    atOnceUsers(10),
    rampUsers(10) during (5 seconds),
    constantUsersPerSec(20) during (15 seconds),
    constantUsersPerSec(20) during (15 seconds) randomized,
    // rampUsersPerSec(10) to 20 during (10 minutes),
    // rampUsersPerSec(10) to 20 during (10 minutes) randomized,
    // heavisideUsers(1000) during (20 seconds) // 8
  ).protocols(httpConf))*/

  setUp(scn.inject(
    constantConcurrentUsers(50) during(30 seconds),
    rampConcurrentUsers(50) to(100) during(20 seconds),
    constantConcurrentUsers(100) during(30 seconds),
    rampConcurrentUsers(100) to(150) during(20 seconds),
    constantConcurrentUsers(150) during(30 seconds)
  ).protocols(httpConf))
    .maxDuration(4 minutes)


  /*setUp(scn.inject(
    incrementConcurrentUsers(5)
      .times(5)
      .eachLevelLasting(10 seconds)
      .separatedByRampsLasting(10 seconds)
      .startingFrom(10)
  ).protocols(httpConf))*/

  /*setUp(scn.inject(constantUsersPerSec(100) during (30 minutes))).throttle(
    reachRps(100) in (10 seconds),
    holdFor(1 minute),
    jumpToRps(50),
    holdFor(2 hours)
  )*/

}
