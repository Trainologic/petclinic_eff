package com.trainologic.samples.petclinic.repository
import com.trainologic.samples.petclinic._
import Stacks._
import model.Owner
import model.Pet
import model.PetType
import org.atnos.eff.Fx
import org.atnos.eff.Eff
abstract class OwnerRepository[M[_]] {
  type S = Fx.prepend[M, BasicStack]

  def findByLastName(lastName: String): Eff[S, Seq[Owner]]
  def findById(id: Int): Eff[S, Owner]
  def save(owner: Owner): Eff[S, Owner]
}

abstract class PetRepository[M[_]] {
  type S = Fx.prepend[M, BasicStack]

  def findPetTypes: Eff[S, Seq[PetType]]
  def findById(id: Int): Eff[S, Pet]
  def save(owner: Pet): Eff[S, Pet]
}