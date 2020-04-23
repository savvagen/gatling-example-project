package scenarios
import java.util.Locale

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import io.gatling.http.Predef._
import io.gatling.core.Predef._
import models.User
import scala.concurrent.duration._

object UsersScenarios {

  object UserScenario {

    var faker = new Faker(new Locale("en-us"))
    var objectMapper = new ObjectMapper()

    val usersChainScn = repeat(3){
      exec(
        http("Get All Users").get("/users").check(
            status.in(200, 304),
            status.not(500),
            status.not(400),
            jsonPath("$[0].id").ofType[Int].is(1)
          )
      )
      .pause(500.milliseconds, 1000.milliseconds)
      .exec {
        val randomId = faker.number().numberBetween(1, 10)
        http("Get Random User").get(s"/users/${randomId}")
          .check(
            status.in(200, 304),
            status.not(500),
            status.not(400),
            jsonPath("$.id").ofType[Int].is(randomId),
            jsonPath("$.name").is(s"user${randomId}")
          )
      }
      .pause(1) // 1 second by default
      .exec {
        val user = new User(101, faker.name().name(), faker.name().username(), faker.internet().emailAddress())
        http("Create User").post("/users")
          .body(StringBody(objectMapper.writeValueAsString(user)))
          .check(
            status.is(201),
            status.not(500),
            jsonPath("$.name").is(s"${user.getName()}")
          )
      }.pause(2)
    }

  }

}