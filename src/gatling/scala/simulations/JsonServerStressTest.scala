package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._


class JsonServerStressTest extends Simulation {


  var httpProtocol = http.baseUrl("http://localhost:3000")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .check(status.not(400), status.not(404), status.not(500))


//  var getUsers = scenario("UsersScenario")
//    .exec(http("getUsers").get("/users").check(status.is(200)))
//    .pause(500 milliseconds)
//    .exec(http("getFirstUser").get("/users/1").check(status.is(200)))
//    .pause(1 second)

  var scn = scenario("MainScenario").exec(Posts.getPosts, Users.getUsers)

  setUp(scn.inject(
    constantUsersPerSec(50) during(60 seconds)).throttle(
      reachRps(10) in(5 seconds),
      holdFor(10 seconds),
      reachRps(30) in(5 seconds),
      holdFor(10 seconds),
      reachRps(50) in(5 seconds), holdFor(15 seconds)
  )).protocols(httpProtocol)


}

object Users {

  val getUsers = exec(http("getUsers").get("/users").check(status.is(200)))
    .pause(500 milliseconds)
    .exec(http("getFirstUser").get("/users/1").check(status.is(200)))
    .pause(1 second)
}


object Posts {

  val getPosts = exec(http("getPosts").get("/posts").check(status.is(200)))
    .pause(500 milliseconds)
    .exec(http("getFirstPost").get("/posts/1").check(status.is(200)))
    .pause(700 milliseconds)
}