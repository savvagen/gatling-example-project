package simulations.tutorials

import baseConf.BaseSimulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._


class MyFirstSimmulation extends BaseSimulation  {

  val myScenario = scenario("First Load Test")
      .repeat(3){
        exec(http("get users").get("/users")
          .check(status.in(200, 304)))
          .pause(1)
        .exec(http("get specific user").get("/users/77")
          .check(status.in(200, 304)))
          .pause(1)
      }.repeat(2){
        exec(http("get posts").get("/posts")
          .check(status.in(200, 304)))
          .pause(1)
        .exec(http("get specific post").get("/posts/77")
          .check(status.in(200, 304)))
          .pause(500.milliseconds)
  }

  setUp(myScenario.inject(atOnceUsers(1)))
    .protocols(httpConf)


}
