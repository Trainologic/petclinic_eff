package com.trainologic.samples.petclinic.web

import org.atnos.eff._
import org.atnos.eff.syntax.eff._
import org.atnos.eff.ReaderEffect._
import org.atnos.eff.TaskEffect._
import scalaz.\/
import scalaz.Reader
import scalaz.~>
import scalaz.concurrent.Task
import org.http4s._, org.http4s.dsl._
import com.trainologic.samples.petclinic._
import service.ClinicService
import repository.OwnerRepository
import model.Owner

class OwnerController[M[_]] {
  object LastNameQueryParamMatcher extends QueryParamDecoderMatcher[String]("lastName")
  type S1 = Fx.fx5[M, DataAccessException \/ ?, Validate[String, ?], Reader[ClinicService[M], ?], Reader[OwnerRepository[M], ?]]
  type S2 = Fx.fx5[Task, DataAccessException \/ ?, Validate[String, ?], Reader[ClinicService[M], ?], Reader[OwnerRepository[M], ?]]
  implicit def ownersEncoder: EntityEncoder[Seq[Owner]] = ???

  // name taken from petclinic controller
  def processFindForm(implicit ev: M ~> Task): Request => Eff[S2, Response] = {
    case request @ GET -> Root / "owners" :? LastNameQueryParamMatcher(lastName) => for {
      service <- ask[S2, ClinicService[M]].into[S2]
      owners <- service.findOwnerByLastName(lastName).transform(ev).into[S2]
      r <- doTask[S2, Response](Ok(owners))
    } yield r
  }
}