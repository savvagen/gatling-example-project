package simulations.tutorials

import baseConf.BaseSimulation
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import models.{JavaUser, User}



class CheckResponseCode extends BaseSimulation {

  var objectMapper = new ObjectMapper()
  var gson = new Gson()

  val scn = scenario("Body Check Test")
    .exec(http("get specific user").get("/users/1")
      .check(status.in(200, 304))
      .check(jsonPath("$.name").is("user1"))
      .check(jsonPath("$.username").is("user_name1"))
      .check(jsonPath("$.email").is("user1@test.com"))
    )
    .pause(1)
    .exec(http("get users").get("/users")
      .check(status.in(200, 304))
      .check(jsonPath("$[1].id").saveAs("userId"))
    ).exec(http("get specific user with parameter").get("/users/${userId}")
    .check(status.in(200, 304))
    .check(jsonPath("$.id").saveAs("${userId}"))
    .check(jsonPath("$.name").saveAs("user${userId}"))
  )


  def getAllUsers()= {
    repeat(3){
      exec(http("get all users")
        .get("/users")
        .check(status.in(200, 304)))
    }

  }

  def getSpecificUser() = {
    repeat(2){
      exec(http("get aspecific user")
        .get("/users/1")
        .check(status.in(200, 304)))
    }
  }

  val simpleScenario  = scenario("Test Users")
      .exec(getAllUsers())
        .pause(1)
        .exec(getSpecificUser())
        .pause(1)

  setUp(
    simpleScenario.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
