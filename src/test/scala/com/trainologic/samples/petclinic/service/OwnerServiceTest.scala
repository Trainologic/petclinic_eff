package com.trainologic.samples.petclinic.service
import com.trainologic.samples.petclinic._
import repository.MapBasedReadOnlyOwnerRepository
import monix.execution.Scheduler.Implicits.global
import cats.~>
import cats.Id
import cats.data.Xor
import cats.data.Reader
import monix.eval.Task
import model.Owner
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import repository.OwnerRepository
import org.atnos.eff._
import monix.TaskEffect._
import repository.OwnerRepositoryDoobieH2
import org.h2.jdbcx.JdbcConnectionPool
import doobie.imports._
import web.OwnerController
import org.http4s.Response
import org.http4s.server.blaze.BlazeBuilder
import cats.syntax.applicative._
import cats.syntax.apply._
import cats.Applicative
import cats.implicits._
import fs2.util.Catchable
import fs2.util.Suspendable
import fs2.util.Attempt
import Utils._
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
  val h2Repo: OwnerRepository[ConnectionIO] = new OwnerRepositoryDoobieH2
  val service2: ClinicService[ConnectionIO] = new ClinicServiceImpl[ConnectionIO]

  def helper(es: Eff[controller.S2, Response]): scalaz.concurrent.Task[Response] = {

    val gggg = runReader(service2)(es)
    val hhhh = runReader(h2Repo)(gggg)

    type TS = Fx2[Task, DataAccessException Xor ?]
    /// merge validate with either
    import cats.instances.string._
    val handledValidation = runMap(hhhh)((_: String) + " ")

    /*val z = new Interpret.Translate[Validate[String, ?], TS] {
      override def apply[X](vv: Validate[String, X]): Eff[TS, X] = {
         
        ???
      }
    }*/
    val jj = for {
      rv <- handledValidation
      f <- fromXor(rv).into[TS]
    } yield f

    val w = new Interpret.Translate[DataAccessException Xor ?, Fx1[Task]] {
      override def apply[X](vv: DataAccessException Xor X): Eff[Fx1[Task], X] =
        for {
          // TODO: replace with Task.raiseError
          x <- monix.TaskEffect.async { vv.fold(x => throw new RuntimeException(x), identity) }
        } yield x

    }

    val bbbaaa = jj.translate(w)(Member.Member2R[Task, String Xor ?])
    
    bbbaaa.detach
    
  }

  import org.http4s._, org.http4s.dsl._

  val dummyReq = Request(uri = Uri(path = "/owners", query = Query("lastName" -> Some("john"))))
//  val muuu = controller.processFindForm(natTransform(xa))
//  val blbl = muuu(dummyReq)
 // helper(blbl)

 // System.exit(0)
  val test4 = HttpService(controller.processFindForm(natTransform(xa)) andThen helper)

  BlazeBuilder.mountService(test4, "/owners").run

  Thread.sleep(32432442)

  def test1 = {
    val owners: Map[Int, Owner] = Map(
      1 -> Owner(Some(1), "john", "Davis", "TA", "TA", "0000"),
      2 -> Owner(Some(2), "john2", "Davis", "TA", "TA", "0000"),
      3 -> Owner(Some(3), "john3", "Bavis", "TA", "TA", "0000"))

    

    val service1 = new ClinicServiceImpl[Id]
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

    
    val simpleRepo = MapBasedReadOnlyOwnerRepository(owners)
    
    val baa = runReader(simpleRepo)(check1)    
    
    
    val result = runReader(simpleRepo)(check1).runXor.runNel.runPure
    println(result)

    prepareDB(cp).coeval
    import scala.concurrent.duration._
    val result2 = awaitTask(runReader(h2Repo)(prog2))(20 seconds).runXor.runNel.run

    println(result2)

    val theApplicative: Applicative[Eff[Fx3[Task, String Xor ?, Validate[String, ?]], ?]] = implicitly
    /* for now it crashes compiler after moving to cats
    val theProg = for {
      lb <- theApplicative.replicateA(10, runReader(simpleRepo)(check1))
      lb2 <- theApplicative.replicateA(10, runReader(h2Repo)(prog3))
      lids <- theApplicative.replicateA(10, runReader(h2Repo)(prog2))
    } yield lb ++ lb2 ++ lids

    val results = awaitTask(theProg)(20 seconds).runDisjunction.runNel.run
    println(results)*/
  }
  test1
}