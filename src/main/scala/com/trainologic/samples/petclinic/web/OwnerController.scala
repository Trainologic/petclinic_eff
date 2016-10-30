package com.trainologic.samples.petclinic.web

import monix.eval.Task
import cats.~>
import argonaut._
import org.atnos.eff._
import syntax.eff._
import ReaderEffect._
import monix.TaskEffect._
import org.http4s._, org.http4s.dsl._
import argonaut._
import com.trainologic.samples.petclinic._
import service.ClinicService
import repository.OwnerRepository
import model.Owner
import cats.data.Xor
import cats.data.Reader
import model.ArgonautCodecs._
import com.trainologic.samples.petclinic.repository.PetRepository
import com.trainologic.samples.petclinic.model.PetType

class OwnerController[M[_]] {

  object LastNameQueryParamMatcher extends QueryParamDecoderMatcher[String]("lastName")
  type BaseOwnerStack = Fx.fx4[DataAccessException Xor ?, Validate[String, ?], Reader[ClinicService[M], ?], Reader[OwnerRepository[M], ?]]
  type BasePetStack = Fx.fx4[DataAccessException Xor ?, Validate[String, ?], Reader[ClinicService[M], ?], Reader[PetRepository[M], ?]]
  //type S1 = FxAppend[Fx1[M], BaseStack]
  type S2 = FxAppend[Fx1[Task], BaseOwnerStack]
  type S3 = FxAppend[Fx1[Task], BasePetStack]

  implicit def ownersEncoder: EntityEncoder[Seq[Owner]] = jsonEncoderOf[Seq[Owner]]
  implicit def petTypesEncoder: EntityEncoder[Seq[PetType]] = jsonEncoderOf[Seq[PetType]]

  def populatePetTypes(implicit ev: M ~> Task): PartialFunction[Request, Eff[S3, Response]] = {
    case request @ GET -> Root / "petTypes" => for {
      service <- ask[S3, ClinicService[M]].into
      petTypes <- service.findPetTypes.transform(ev).into[S3]
      r <- async[S3, Response](Ok(petTypes).unsafePerformSync)
    } yield r

  }

  def processFindForm(implicit ev: M ~> Task): PartialFunction[Request, Eff[S2, Response]] = {
    case request @ GET -> Root / "owners" :? LastNameQueryParamMatcher(lastName) => for {
      service <- ask[S2, ClinicService[M]].into
      owners <- service.findOwnerByLastName(lastName).transform(ev).into[S2]
      r <- async[S2, Response](Ok(owners).unsafePerformSync)
    } yield r

  }

}