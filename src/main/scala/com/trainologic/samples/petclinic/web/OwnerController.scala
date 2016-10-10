package com.trainologic.samples.petclinic.web

import argonaut._
import org.atnos.eff._
import syntax.eff._
import ReaderEffect._
import TaskEffect._
import scalaz.\/
import scalaz.Reader
import scalaz.~>
import scalaz.concurrent.Task
import org.http4s._, org.http4s.dsl._
import argonaut._
import com.trainologic.samples.petclinic._
import service.ClinicService
import repository.OwnerRepository
import model.Owner

class OwnerController[M[_]] {
  object LastNameQueryParamMatcher extends QueryParamDecoderMatcher[String]("lastName")
  type BaseStack = Fx.fx4[DataAccessException \/ ?, Validate[String, ?], Reader[ClinicService[M], ?], Reader[OwnerRepository[M], ?]]
  type S1 = FxAppend[Fx1[M], BaseStack]
  type S2 = FxAppend[Fx1[Task], BaseStack]

  implicit val ownerCodecJson: EncodeJson[Owner] =
    Argonaut.casecodec6(Owner.apply, Owner.unapply)("id", "firstName", "lastName", "address", "city", "telephone")

  implicit def ownersEncoder: EntityEncoder[Seq[Owner]] = jsonEncoderOf[Seq[Owner]]

   
  def processFindForm(implicit ev: M ~> Task): PartialFunction[Request,Eff[S2, Response]] = {
    case request @ GET -> Root / "owners" :? LastNameQueryParamMatcher(lastName) => for {
      service <- ask[S2, ClinicService[M]].into 
      owners <- service.findOwnerByLastName(lastName).transform(ev).into[S2]
      r <- doTask[S2, Response](Ok(owners))
    } yield r
  }
}