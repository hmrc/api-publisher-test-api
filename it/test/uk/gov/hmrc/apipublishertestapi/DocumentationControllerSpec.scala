/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apipublishertestapi

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

class DocumentationControllerSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach
    with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl  = s"http://localhost:$port"

  override def beforeEach(): Unit = {
    super.beforeEach()
    wsClient
      .url(s"$baseUrl/set-definition")
      .post(Json.obj("location" -> "notfound"))
      .futureValue
  }

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .build()

  "definition endpoint" should {

    "respond with 204 status when changing definition" in {
      val response =
        wsClient
          .url(s"$baseUrl/set-definition")
          .post(Json.obj("location" -> "v1_alpha.json"))
          .futureValue

      response.status shouldBe 204
    }

    "respond with 404 status on startup" in {
      val response =
        wsClient
          .url(s"$baseUrl/api/definition")
          .get()
          .futureValue

      response.status shouldBe 404
    }

    "respond with 200 status after changing definition" in {
      wsClient
        .url(s"$baseUrl/set-definition")
        .post(Json.obj("location" -> "v1_alpha.json"))
        .futureValue

      val response = wsClient
        .url(s"$baseUrl/api/definition")
        .get()
        .futureValue

      response.status shouldBe 200
      response.json shouldBe Json.parse("""{
                                          |  "api": {
                                          |    "name": "Publisher Test",
                                          |    "description": "An api that is used to test publishing flows",
                                          |    "context": "test/publisher",
                                          |    "categories": [
                                          |      "OTHER"
                                          |    ],
                                          |    "versions": [
                                          |      {
                                          |        "version": "1.0",
                                          |        "status": "ALPHA",
                                          |        "endpointsEnabled": false
                                          |      }
                                          |    ]
                                          |  }
                                          |}""".stripMargin)
    }

  }
}
