package simulations

import io.gatling.core.Predef.{reachRps, _}
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scenarios.UsersScenarios.UserScenario
import scenarios.PostsScenarios.PostScenario

class LoadTest extends Simulation {


  val usersScn = scenario("Users Scenario").exec(UserScenario.usersChainScn)
  var postsScn = scenario("Post Scenario").exec(PostScenario.postsChanScn)

  var httpConf = http
    .baseUrl("http://localhost:3000")
    .header("Content-Type", "application/json")

  setUp(
    usersScn.inject(
      rampUsers(30) during(10 seconds),
      constantUsersPerSec(30) during(20)),
    postsScn.inject(
      rampUsers(30) during(10 seconds),
      constantUsersPerSec(30) during(20)
    ),
  ).protocols(httpConf)
    .assertions(
      global.responseTime.max.lte(6000),
      global.successfulRequests.percent.gt(95),
      global.failedRequests.count.is(0)
    )

  /*setUp(
    usersScn.inject(
      constantUsersPerSec(150) during(80 seconds)).throttle(
        reachRps(50) in(10 seconds),
        holdFor(10 seconds),
        reachRps(100) in(10 seconds),
        holdFor(10 seconds),
        reachRps(150) in(10 seconds),
        holdFor(30 seconds)
    ),
    postsScn.inject(
      constantUsersPerSec(150) during(80 seconds)).throttle(
        reachRps(50) in(10 seconds),
        holdFor(10 seconds),
        reachRps(100) in(10 seconds),
        holdFor(10 seconds),
        reachRps(150) in(10 seconds),
        holdFor(30 seconds)
    )
  )
    .protocols(httpConf)
    .assertions(
      global.responseTime.max.lte(6000),
      global.successfulRequests.percent.gt(95),
      global.failedRequests.count.is(0)
    )*/

}
