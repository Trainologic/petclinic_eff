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
class OwnerController[M[_]] {
	
  object LastNameQueryParamMatcher extends QueryParamDecoderMatcher[String]("lastName")
  type BaseStack = Fx.fx4[DataAccessException Xor ?, Validate[String, ?], Reader[ClinicService[M], ?], Reader[OwnerRepository[M], ?]]
  type S1 = FxAppend[Fx1[M], BaseStack]
  type S2 = FxAppend[Fx1[Task], BaseStack]

  implicit val ownerCodecJson: EncodeJson[Owner] =
    Argonaut.casecodec6(Owner.apply, Owner.unapply)("id", "firstName", "lastName", "address", "city", "telephone")

    
  implicit def ownersEncoder: EntityEncoder[Seq[Owner]] = jsonEncoderOf[Seq[Owner]]

   
  def processFindForm(implicit ev: M ~> Task): PartialFunction[Request,Eff[S2, Response]] = {
    case request @ GET -> Root / "owners" :? LastNameQueryParamMatcher(lastName) => for {
      service <- ask[S2, ClinicService[M]].into 
      owners <- service.findOwnerByLastName(lastName).transform(ev).into[S2]
      r <- async[S2, Response](Ok(owners).unsafePerformSync)
    } yield r
    
  }
  
  
}