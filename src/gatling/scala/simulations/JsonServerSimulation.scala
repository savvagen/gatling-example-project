package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._


class JsonServerSimulation extends Simulation {

  var httpConf = http
    .baseUrl("http://localhost:3000")
    .header("Content-Type", "application/json")

  /*var userScenario = scenario("Get Users")
    .exec(http("get_users").get("/users").check())
    .pause(1)
    .exec(http("get_user").get("/users/1").check())
    .pause(500 millisecond)*/

  /*var userScenario = scenario("Get Users")
    .repeat(2) {
      exec(http("get_user").get("/users/1").check())
    }
    .pause(1)
    .repeat(1) {
      exec(http("get_users").get("/users").check())
    }
    .pause(500 millisecond)*/

  var postsScenario = scenario("Test Posts")
      .exec(http("get_posts").get("/posts").check(status.not(400), status.not(500)))
      .pause(1 second)
      .exec(http("get_post").get("/posts/1").check(status.is(200), jsonPath("$.title").is("post 1")))
      .pause(500 milliseconds)


  setUp(postsScenario.inject(
    nothingFor(1),
    rampUsers(30) during(5 seconds),
    constantUsersPerSec(30) during (15 seconds) randomized
  )).protocols(httpConf)

  /*setUp(postsScenario.inject(
    constantUsersPerSec(150) during (10 second)).throttle(
      reachRps(100) in (10 seconds),
      holdFor(20 second),
      jumpToRps(50),
      holdFor(10 second )
    )).protocols(httpConf)*/

  /*setUp(postsScenario.inject(
    constantUsersPerSec(50) during (30 second)).throttle(
    reachRps(50) in (10 seconds), holdFor(20 second),
    reachRps(100) in (10 seconds), holdFor(20 seconds),
    jumpToRps(150), holdFor(20 second )
  )).protocols(httpConf)*/

  /*setUp(postsScenario.inject(constantUsersPerSec(100) during (30 minutes))).throttle(
    reachRps(100) in (10 seconds),
    holdFor(1 minute),
    jumpToRps(50),
    holdFor(2 hours)
  )*/

  /*setUp(
    postsScenario.inject(
      nothingFor(4 seconds), // 1
      atOnceUsers(10), // 2
      rampUsers(50) during (5 seconds), // 3
      constantUsersPerSec(50) during (15 seconds), // 4
      rampUsers(100) during (5 seconds),
      constantUsersPerSec(100) during (15 seconds) randomized, // 5
      rampUsersPerSec(10) to 20 during (10 minutes), // 6
      rampUsersPerSec(10) to 20 during (10 minutes) randomized, // 7
      heavisideUsers(1000) during (20 seconds) // 8
    ).protocols(httpConf)
  )*/

  before(
    println("HELLO SAVVA!!!!!!!")
  )

  after(
    println("HELLO SAVVA!!!!!!!!!!")
  )



}