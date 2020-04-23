package simulations.tutorials

import baseConf.BaseSimulation
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.http.Predef._
import models.{JavaUser, User}
import scenarios.UsersScenarios.UserScenario

import scala.concurrent.duration._


class SimmulationWithBodyChecks extends BaseSimulation {

  val varScenario = scenario("Body Check Test")
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

  var objectMapper = new ObjectMapper()
  var gson = new Gson()

  val listScenario = scenario("List Body Check Test")
    // Get All users and save user + list of users
    .exec(http("get list of users").get("/users")
      .check(status.in(200, 304))
      .check(jsonPath("$[1]").saveAs("savedUser"))
      .check(bodyString.saveAs("users"))
      //.check(jsonPath("$[*].id").findAll.saveAs("ids")) // Save as object: Vector[String]
    )
    .pause(1)
    // Extract deserialize uses list and write selected id to new session property
    .exec( session => {
      val jsonList = session("users").as[String]
      val usersList = gson.fromJson(jsonList, classOf[Array[User]])
      val myId = usersList(6).getId()
      println(objectMapper.writeValueAsString(usersList(6)))
      session.set("selected", myId)
    })
    // Execute requests using "selected" and "savedUser" session properties
    // Get user with "selected" id
    .exec(
      http("get user from selected id: ${selected}").get("/users/${selected}")
        .check(status.in(200, 304), jsonPath("$.id").is("${selected}")))
    .pause(1)
    // Update user with saved user json
    .exec(
      http("update user").put("/users/2")
        .body(StringBody("${savedUser}")).check(status.in(200, 304)))
    .pause(1)
    .exec(session => { println(session("savedUser").as[String]); session }  )
    .exec(session => { println(session("selected").as[String]); session }  )
    .exec(session => {
      val userJson = session("savedUser").as[String]
      val javaUser = gson.fromJson(userJson, classOf[JavaUser])
      javaUser.setId(120)
      javaUser.setName("NewUser")
      javaUser.setUsername("New.User.Modified")
      session.set("userModified", gson.toJson(javaUser))  })
    .exec(session=> { println(session("userModified").as[String]); session })




  setUp(
    //varScenario.inject(atOnceUsers(1)),
    listScenario.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
