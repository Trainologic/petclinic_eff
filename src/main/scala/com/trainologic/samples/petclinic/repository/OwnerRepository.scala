package com.trainologic.samples.petclinic.repository
import com.trainologic.samples.petclinic._
import org.atnos.eff.Fx2
import org.atnos.eff.Fx
import model.Owner
import org.atnos.eff.Eff
import org.atnos.eff.Validate
import cats.data.Xor
import cats.Monad

abstract class OwnerRepository[M[_] : Monad] {
   
  type S = Fx.fx3[M, DataAccessException Xor ?, Validate[String, ?]]
  
  def findByLastName(lastName: String):  Eff[S, Seq[Owner]]
  def findById(id: Int) : Eff[S, Owner]
  def save(owner: Owner): Eff[S, Owner]
  
}