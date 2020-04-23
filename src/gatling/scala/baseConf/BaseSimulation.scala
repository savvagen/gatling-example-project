package baseConf

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BaseSimulation extends Simulation {

  var httpConf = http.baseUrl("http://localhost:3000")
    .contentTypeHeader("application/json")
    .acceptCharsetHeader("utf-8")
    .acceptHeader("application/json")
    //.proxy(Proxy("localhost", 8888).httpsPort(8888)) // Send All requests through Fiddler or Charles proxy



}
