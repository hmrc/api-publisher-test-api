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

package uk.gov.hmrc.apipublishertestapi.controllers

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future.successful

import controllers.Assets

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

case class DefinitionLocation(location: String)

object DefinitionLocation {
  implicit val format: OFormat[DefinitionLocation] = Json.format[DefinitionLocation]
}

@Singleton
class DocumentationController @Inject() (assets: Assets, cc: ControllerComponents) extends BackendController(cc) {
  private var definitionLocation: String                 = "notfound.json"
  private var maybeSpecificationLocation: Option[String] = None

  def definition(): Action[AnyContent] = {
    assets.at("/public/api/definitions", definitionLocation)
  }

  def setDefinition(): Action[DefinitionLocation] = Action.async(parse.json[DefinitionLocation]) { implicit request =>
    definitionLocation = request.body.location
    successful(NoContent)
  }

  def specification(version: String, file: String): Action[AnyContent] = {
    maybeSpecificationLocation match {
      case Some(specificationLocation) if (file == "application.yaml") => assets.at(s"/public/api/conf/1.0", specificationLocation)
      case _                                                           => assets.at(s"/public/api/conf/1.0", file)
    }
  }

  def setSpecification(): Action[DefinitionLocation] = Action.async(parse.json[DefinitionLocation]) { implicit request =>
    maybeSpecificationLocation = Some(request.body.location)
    successful(NoContent)
  }
}
