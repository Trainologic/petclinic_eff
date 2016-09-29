package com.trainologic.samples.petclinic.repository
import doobie.imports._
import org.atnos.eff._
import Eff._
import org.atnos.eff.syntax.eff._
import TaskEffect._
import com.trainologic.samples.petclinic._
import model.Owner
import scalaz.concurrent.Task
import java.sql.Connection
import org.h2.jdbcx.JdbcConnectionPool

class OwnerRepositoryDoobieH2(val transactor: Transactor[Task]) extends OwnerRepository[ConnectionIO] {
  override def findById(id: Int): Eff[S, Owner] = {
    ???
  }

  override def findByLastName(lastName: String): Eff[S, Seq[Owner]] = {
    ???
  }

  def insertOwner(owner: Owner) = sql"""
      INSERT INTO 
        owners (first_name, last_name, address, city, telephone) 
        VALUES
        (${owner.firstName} , ${owner.lastName}, ${owner.address} , ${owner.city} ,
        ${owner.telephone} )
     """.update.withUniqueGeneratedKeys[Int]("id")

     

  override def save(owner: Owner): Eff[S, Owner] = for {
    id <- send(insertOwner(owner)).into[S]
    result = owner.copy(id = Some(id))
  } yield result
}