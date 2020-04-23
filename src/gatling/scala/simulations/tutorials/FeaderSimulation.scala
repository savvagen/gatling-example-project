package simulations.tutorials

import baseConf.BaseSimulation
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import models.{JavaUser, User}
import scenarios.UsersScenarios.UserScenario
import scala.concurrent.duration._

import baseConf.BaseSimulation

class FeaderSimulation  extends BaseSimulation{

  val csvDtata = csv("data/users.csv").eager.random

  def getSpecificUser() = {
    repeat(10){
      feed(csvDtata)
        .exec(http("get specific user").get("/users/${id}")
        .check(
          status.in(200, 304),
          jsonPath("$.id").ofType[Int].is("${id}"),
          jsonPath("$.name").is("${name}"),
          jsonPath("$.username").is("${username}"),
          jsonPath("$.email").is("${email}")))
        .pause(500.milliseconds)
    }
  }

  val mapData = Array(
    Map("id" -> 1, "title" -> "post 1"),
    Map("id" -> 2, "title" -> "post 2"),
    Map("id" -> 3, "title" -> "post 3"),
    Map("id" -> 4, "title" -> "post 4"),
    Map("id" -> 5, "title" -> "post 5")
  ).circular

  def getSpecificPost(postId: String, postTitle: String)= {
    exec(http("get specific post").get(s"/posts/${postId}")
      .check(
        status.in(200, 304),
        jsonPath("$.title").is(s"${postTitle}")))
  }

  val users = scenario("Users Scenario").
    exec(getSpecificUser())

  val posts = scenario("Posts Scenario")
    .repeat(5){
      feed(mapData)
        .exec(getSpecificPost("${id}", "${title}"))
        .pause(500.milliseconds)
    }


  setUp(
    users.inject(atOnceUsers(50)),
    posts.inject(atOnceUsers(50))
  ).protocols(httpConf)



}
