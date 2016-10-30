package com.trainologic.samples.petclinic.service
import com.trainologic.samples.petclinic._
import model.Owner
import cats.data.Reader
import repository.OwnerRepository
import cats.data.Xor
import org.atnos.eff._
import org.atnos.eff.syntax.eff._
import org.atnos.eff.ReaderEffect._
import org.atnos.eff.ValidateEffect._
import cats.Monad
import com.trainologic.samples.petclinic.model.Pet
import com.trainologic.samples.petclinic.model.PetType
import com.trainologic.samples.petclinic.Stacks.BasicStack
import com.trainologic.samples.petclinic.repository.PetRepository

abstract class ClinicService[M[_]] {

  type OwnerStack = Fx.fx4[M, DataAccessException Xor  ?, Validate[String, ?], Reader[OwnerRepository[M], ?]]
  type PetStack = Fx.prepend[M, Fx.prepend[Reader[PetRepository[M], ?], BasicStack]]
  
  def saveOwner(owner: Owner): Eff[OwnerStack, Owner]
  def savePet(pet: Pet): Eff[PetStack, Pet]

  def findPetTypes: Eff[PetStack, Seq[PetType]]

  def findOwnerByLastName(lastName: String): Eff[OwnerStack, Seq[Owner]]

  def findOwnerById(id: Int): Eff[OwnerStack, Owner]
  def findPetById(id: Int): Eff[PetStack, Pet]
}

class ClinicServiceImpl[M[_]] extends ClinicService[M] {

   override def findPetById(id: Int) = for {
    c <- ask[PetStack, PetRepository[M]]
    pet <- c.findById(id).into[PetStack]
  } yield pet
  
  
  override def findPetTypes() = for {
    c <- ask[PetStack, PetRepository[M]]
      petTypes <- c.findPetTypes.into[PetStack]
  } yield petTypes
  
  
  override def findOwnerById(id: Int) = for {
    c <- ask[OwnerStack, OwnerRepository[M]]
    owner <- c.findById(id).into[OwnerStack]
  } yield owner

  override def findOwnerByLastName(lastName: String) = for {
    c <- ask[OwnerStack, OwnerRepository[M]]
    r <- c.findByLastName(lastName).into[OwnerStack]
  } yield r

  
  def validatePet(pet: Pet) = for {
    _ <- validateCheck(pet.name.nonEmpty, "name should not be empty")
  } yield ()
  
  
  def validateOwner(owner: Owner) = {
   /* import cats.implicits._
    val boo = List(owner.lastName -> "last name",
      owner.firstName -> "first name",
      owner.address -> "address",
      owner.city -> "city").
      map { case (k, v) => validateCheck(k.nonEmpty, v + " should not be empty") }.sequence
*/
    for {
      _ <- validateCheck(owner.lastName.nonEmpty, "last name should not be empty")
      _ <- validateCheck(owner.firstName.nonEmpty, "first name should not be empty")
      _ <- validateCheck(owner.address.nonEmpty, "address should not be empty")
      _ <- validateCheck(owner.city.nonEmpty, "city should not be empty")
      // telephone -- number no fractions, max 10 digits
    } yield ()
  }

  override def savePet(pet: Pet): Eff[PetStack, Pet] = for {
    petRepository <- ask[PetStack, PetRepository[M]]
    _ <- validatePet(pet).into[PetStack]
    newPet <- petRepository.save(pet).into[PetStack]
  } yield newPet
  
  
  override def saveOwner(owner: Owner): Eff[OwnerStack, Owner] = for {
    ownerRepository <- ask[OwnerStack, OwnerRepository[M]]
    _ <- validateOwner(owner).into[OwnerStack]
    newOwner <- ownerRepository.save(owner).into[OwnerStack]
  } yield newOwner

}