package com.trainologic.samples.petclinic.service
import com.trainologic.samples.petclinic._
import model.Owner
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import repository.OwnerRepository
import org.atnos.eff._
import org.atnos.eff.task._
import scalaz.\/-
import scalaz.\/
import repository.OwnerRepositoryDoobieH2
import org.h2.jdbcx.JdbcConnectionPool
import doobie.imports._
import scalaz.~>
import scalaz.concurrent.Task
import scalaz.syntax.monad._
import scalaz.NaturalTransformation
import com.trainologic.samples.petclinic.web.OwnerController
import org.http4s.Response
import org.http4s.server.blaze.BlazeBuilder

object OwnerServiceTest extends App {

   def natTransform(tran: Transactor[Task]) = new (ConnectionIO ~> Task) {
    def apply[A](o: ConnectionIO[A]) = o.transact(tran)
  }

  def prepareDB(cp: JdbcConnectionPool): Task[Int] = {

    val xa = DataSourceTransactor[Task](cp)

    import repository.InitH2._
    import repository.PopulateH2._
    (dropAll.run *> createTables.run *> populateDB.run).transact(xa)
  }

  
  
  
  val controller = new OwnerController[ConnectionIO]
  
  val cp = JdbcConnectionPool.create("jdbc:h2:~/test", "sa", "sa")

    val xa = DataSourceTransactor[Task](cp)
    val h2Repo: OwnerRepository[ConnectionIO] = new OwnerRepositoryDoobieH2(xa)
val service2 : ClinicService[ConnectionIO] = new ClinicServiceImpl[ConnectionIO]
  
  def helper(es: Eff[controller.S2, Response]) : Task[Response] = {
  
    val gggg = runReader(h2Repo)(es)
    val hhhh = runReader(service2)(gggg)
    
    type TS = Fx2[Task, DataAccessException \/ ?]
    /// merge validate with either
    val z = new Interpret.Translate[Validate[String, ?], TS] {
      override def apply[X](vv: Validate[String, X]) : Eff[TS, X] = ???
    } 
    val qqqq = hhhh.translate[Validate[String, ?], TS](z)
    
    val w = new Interpret.Translate[DataAccessException \/ ?, Fx1[Task]] {
      override def apply[X](vv: DataAccessException \/ X) : Eff[Fx1[Task], X] = ???
    }
    
    val bbbaaa = qqqq.translate(w)
    bbbaaa.detach
    
  }
  
  
  
  import org.http4s._, org.http4s.dsl._
  val test4 = HttpService(controller.processFindForm(natTransform(xa)) andThen helper)
  
  BlazeBuilder.mountService(test4, "/owners").run
  
  Thread.sleep(32432442)
  
  
  
  
  def test1 = {
    val owners: Map[Int, Owner] = Map(
      1 -> Owner(Some(1), "john", "Davis", "TA", "TA", "0000"),
      2 -> Owner(Some(2), "john2", "Davis", "TA", "TA", "0000"),
      3 -> Owner(Some(3), "john3", "Bavis", "TA", "TA", "0000"))

    val simpleRepo: OwnerRepository[Task] = new OwnerRepository[Task] {

      def fromLastName(lastName: String) =
        owners.values.filter(_.lastName == lastName).toSeq

      override def findByLastName(lastName: String): Eff[S, Seq[Owner]] =
        doNow(fromLastName(lastName))

      override def findById(id: Int): Eff[S, Owner] = ???
      override def save(owner: Owner): Eff[S, Owner] = ???
    }

    val service1 = new ClinicServiceImpl[Task]
    val check1 = for {
      owners <- service1.findOwnerByLastName("Davis")
    } yield owners.size == 2

    

    val check2 = for {
      nowner <- service2.saveOwner(Owner(None, "john", "smith", "ta", "ta", "4444"))
    } yield nowner.id

    val check3 = for {
      owners <- service2.findOwnerByLastName("Davis")
    } yield owners.size == 2

    
    val prog2 = check2.transform(natTransform(xa))
    val prog3 = check3.transform(natTransform(xa))

  
    import scala.concurrent.duration._
    val result = attemptTask(runReader(simpleRepo)(check1))(20 seconds).runDisjunction.runNel.run
    println(result)

    prepareDB(cp).unsafePerformSync
    val result2 = attemptTask(runReader(h2Repo)(prog2))(20 seconds).runDisjunction.runNel.run

    println(result2)

    val theProg = for {
      lb <- runReader(simpleRepo)(check1).replicateM(10)
      lb2 <- runReader(h2Repo)(prog3).replicateM(10)
      lids <- runReader(h2Repo)(prog2).replicateM(10)
    } yield lb ++ lb2 ++ lids

    val results = attemptTask(theProg)(20 seconds).runDisjunction.runNel.run
    println(results)
  }
  test1
}