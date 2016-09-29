package com.trainologic.samples.petclinic.service

import com.trainologic.samples.petclinic.model.Owner
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import com.trainologic.samples.petclinic.repository.OwnerRepository
import org.atnos.eff.Eff
import org.atnos.eff.task._
import scalaz.\/-
import com.trainologic.samples.petclinic.repository.OwnerRepositoryDoobieH2
import org.h2.jdbcx.JdbcConnectionPool
import doobie.imports._
import scalaz.concurrent.Task
import scalaz.syntax.monad._

object OwnerServiceTest extends App {

  
  def prepareDB(cp: JdbcConnectionPool) : Task[Int] = {
    
    val xa = DataSourceTransactor[Task](cp)
    
    val drop: Update0 =
          sql"""
              DROP TABLE IF EXISTS owners
          """.update

    val create: Update0 =
          sql"""
              CREATE TABLE owners(
                 id bigint auto_increment, 
                 firstName varchar(255), 
                 lastName varchar(255), 
                 address varchar(255), 
                 city varchar(255), 
                 telephone varchar(255)
            )""".update
            
            
    (drop.run *> create.run).transact(xa) 
  }
  
  
  def test1 = {
    val service = new ClinicServiceImpl
    val owners: Map[Int, Owner] = Map(
      1 -> Owner(Some(1), "john", "Davis", "TA", "TA", "0000", Set.empty),
      2 -> Owner(Some(2), "john2", "Davis", "TA", "TA", "0000", Set.empty),
      3 -> Owner(Some(3), "john3", "Bavis", "TA", "TA", "0000", Set.empty))

    val simpleRepo: OwnerRepository = new OwnerRepository {

      def fromLastName(lastName: String) =
        owners.values.filter(_.lastName == lastName).toSeq

      override def findByLastName(lastName: String): Eff[S, Seq[Owner]] =
        doNow(fromLastName(lastName))

      override def findById(id: Int): Eff[S, Owner] = ???
      override def save(owner: Owner): Eff[S, Owner] = ???
    }

    val check1 = for {
      owners <- service.findOwnerByLastName("Davis")
    } yield owners.size == 2

    
    val check2 = for {
      nowner <- service.saveOwner(Owner(None, "john", "smith", "ta", "ta", "4444", Set.empty))
    } yield nowner.id
    
    
    
    val cp = JdbcConnectionPool.create("jdbc:h2:~/test", "sa", "sa")
    
    
    
    val xa = DataSourceTransactor[Task](cp)
    
    val h2Repo : OwnerRepository = new OwnerRepositoryDoobieH2(xa)
    
    
    
    
    
    
    prepareDB(cp).unsafePerformSync
    
    import scala.concurrent.duration._
//    val xxx = runReader(simpleRepo)(check1)
    val xxx = runReader(h2Repo)(check2)
    val yyy = attemptTask(xxx)(20 seconds)
    val kkk = yyy.runDisjunction
    val result = kkk.runNel.run
    //    val vv = result.flatMap(identity)
    println(result)
  }
  test1
}