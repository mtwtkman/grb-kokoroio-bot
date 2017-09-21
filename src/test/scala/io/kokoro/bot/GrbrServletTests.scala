package io.kokoro.bot

import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike

class GrbServletTests extends ScalatraSuite with FunSuiteLike {

  addServlet(classOf[GrbServlet], "/*")

  test("GET / on GrbServlet should return status 200"){
    get("/"){
      status should equal (200)
    }
  }

}
