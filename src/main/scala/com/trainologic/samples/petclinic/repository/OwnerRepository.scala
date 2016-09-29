package com.trainologic.samples.petclinic.repository
import com.trainologic.samples.petclinic._
import org.atnos.eff.Fx2
import org.atnos.eff.Fx
import model.Owner
import scalaz.concurrent.Task
import scalaz.\/
import org.atnos.eff.Eff
import org.atnos.eff.Validate

trait OwnerRepository {
   
  type S = Fx.fx3[ DataAccessException \/ ?, Validate[String, ?], Task]
  
  def findByLastName(lastName: String):  Eff[S, Seq[Owner]]
  def findById(id: Int) : Eff[S, Owner]
  def save(owner: Owner): Eff[S, Owner]
  
}