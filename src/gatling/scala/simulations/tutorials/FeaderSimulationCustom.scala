package simulations.tutorials

import java.util.Locale

import baseConf.BaseSimulation
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import models.{JavaUser, User}
import scenarios.UsersScenarios.UserScenario

import scala.concurrent.duration._
import baseConf.BaseSimulation
import com.github.javafaker.Faker

import scala.util.Random

class FeaderSimulationCustom extends BaseSimulation {

  val idNumbers = (1 to 5).iterator
  val customFeader = Iterator.continually(Map("id" -> idNumbers.next()))

  // Generate Users using custom feader
  val userIds = (1 to 5).iterator
  def getNextUser() = {
    val userId = userIds.next()
    Map("id" -> userId, "name"-> s"user${userId}")
  }
  val usersFeader = Iterator.continually(getNextUser())


  def getSpecificUser() = {
    repeat(5){
      feed(usersFeader)
        .exec(http("get specific user")
          .get("/users/${id}").check(
            status.in(200, 304),
            jsonPath("$.id").ofType[Int].is("${id}"),
            jsonPath("$.name").is("${name}")))
      .pause(100.milliseconds)
    }
  }



  // Random feader
  val randomFeader = Iterator.continually(Map("id" -> Random.nextInt(100)))

  def getRandomUser() = {
    repeat(5){
      feed(randomFeader)
        .exec(http("get specific user")
          .get("/users/${id}").check(
          status.in(200, 304),
          jsonPath("$.id").ofType[Int].is("${id}")))
        .pause(500.milliseconds)
    }
  }


  // Random User feader
  var gson = new Gson()
  var objectMapper = new ObjectMapper()
  var faker = new Faker(new Locale("en_us"))
  var rnd = new Random()

  def randomTextId(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val randomUserFeader = Iterator.continually(Map(
    "name" -> faker.name().fullName(),
    "username" -> faker.name().username(),
    "email" -> faker.internet().emailAddress(randomTextId(10))
  ))


  def createRandomUser() = {
    repeat(5){
      feed(randomUserFeader)
        .exec {
          /*val userJson = """
               {
                  "name": "${name}",
                  "username": "${username}",
                  "email": "${email}"
               }
            """*/
          // val userJson = gson.toJson(new JavaUser("${name}", "${username}", "${email}"))
          http("create random user")
            .post("/users")
            //.body(StringBody(userJson)).asJson
            .body(ElFileBody("templates/user.json")).asJson
            .check(status.is(201))
        }.pause(500.milliseconds)
    }
  }



  val users = scenario("Json Server Scenario")
    .exec(getSpecificUser())
    .exec(getRandomUser())
    .exec(createRandomUser())


  setUp(
    users.inject(atOnceUsers(1))
  ).protocols(httpConf)



}
