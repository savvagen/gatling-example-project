package scenarios

import java.util.Locale

import com.fasterxml.jackson.databind.json.JsonMapper
import com.github.javafaker.Faker
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import models.Post

object PostsScenarios {

  def faker = new Faker(new Locale("en-us"))
  var jsonMapper = new JsonMapper()

  object PostScenario {

    val postsChanScn = repeat(2) {
        exec(
          http("Get All Posts").get("/posts")
            .check(
              status.in(200, 304),
              status.not(500),
              jsonPath("$[0].id").ofType[Int].is(1)
            )
        )
        .pause(1)
        .exec{
          val randomNumber = faker.number().numberBetween(1, 10)
          http("Get Random Post").get(s"/posts/${randomNumber}")
            .check(
              status.in(200, 304),
              jsonPath("$.id").ofType[Int].is(randomNumber),
              jsonPath("$.title").is(s"post ${randomNumber}")
            )
        }
        .pause(1)
        .exec {
          val post = new Post(1, faker.starTrek().character(), faker.starTrek().villain(), 1)
          http("Create Post").post("/posts")
            .body(StringBody(jsonMapper.writeValueAsString(post)))
            .check(
              status.is(201),
              jsonPath("$.title").ofType[String].is(s"${post.title}")
            )
        }.pause(1)
    }


  }

}
