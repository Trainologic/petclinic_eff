package com.trainologic.samples.petclinic.repository

import com.trainologic.samples.petclinic._
import model.Owner
import cats.Id
import org.atnos.eff._
object MapBasedReadOnlyOwnerRepository {
  def apply(owners: Map[Int, Owner]): OwnerRepository[Id] = new OwnerRepository[Id] {

      def fromLastName(lastName: String) =
        owners.values.filter(_.lastName == lastName).toSeq

      override def findByLastName(lastName: String): Eff[S, Seq[Owner]] =
        Eff.pure(fromLastName(lastName))

      override def findById(id: Int): Eff[S, Owner] =
        XorEffect.optionXor(owners.get(id), "id not found")
        
      override def save(owner: Owner): Eff[S, Owner] = 
        XorEffect.left("Updating a read only repository is not supported")
    }
}