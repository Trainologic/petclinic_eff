package com.trainologic.samples.petclinic.service
import com.trainologic.samples.petclinic._
import repository.MapBasedReadOnlyOwnerRepository
import cats.Id
import cats.data.Xor
import cats.data.Reader
import model.Owner
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import repository.OwnerRepository
import org.atnos.eff._
import web.OwnerController
import cats.syntax.applicative._
import cats.syntax.apply._
import cats.Applicative
import cats.implicits._
object OwnerServicePure extends App {

  
  def test1 = {
    val owners: Map[Int, Owner] = Map(
      1 -> Owner(Some(1), "john", "Davis", "TA", "TA", "0000"),
      2 -> Owner(Some(2), "john2", "Davis", "TA", "TA", "0000"),
      3 -> Owner(Some(3), "john3", "Bavis", "TA", "TA", "0000"))

    

    val service1 = new ClinicServiceImpl[Id]
    val check1 = for {
      owners <- service1.findOwnerByLastName("Davis")
    } yield owners.size == 2


    val simpleRepo = MapBasedReadOnlyOwnerRepository(owners)
    
    
    val result = runReader(simpleRepo)(check1).runXor.runNel.runPure
    println(result)

/*
    val theApplicative: Applicative[Eff[Fx3[Task, String Xor ?, Validate[String, ?]], ?]] = implicitly
     for now it crashes compiler after moving to cats
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