package com.trainologic.samples.petclinic
import monix.eval.{ Task => MonixTask }
import scalaz.concurrent.{ Task => ScalazTask }
import monix.execution.Scheduler
import scala.util.{ Try, Right, Left, Either,Success, Failure }
import fs2.util.Suspendable
import fs2.util.Catchable
import fs2.util.Attempt

object Utils {

  implicit class TryPimp[T](t: Try[T]) {
    def toEither: Either[Throwable, T] = t.transform(s => Success(Right(s)), f => Success(Left(f))).get
  }
  
  implicit val taskCatchable  = new Suspendable[MonixTask] with Catchable[MonixTask]{
    override def pure[A](a: A): MonixTask[A] = MonixTask.pure(a)
    override def flatMap[A, B](a: MonixTask[A])(f: A => MonixTask[B]) = a.flatMap(f)
    override def suspend[A](fa: => MonixTask[A]): MonixTask[A] = MonixTask.suspend(fa)
    override def fail[A](err: Throwable): MonixTask[A] = MonixTask.raiseError(err)
    override def attempt[A](fa: MonixTask[A]): MonixTask[Attempt[A]] = fa.materialize.map(_.toEither)
  }
  

  implicit def monixTask2scalazTask[A](mtask: MonixTask[A])(implicit s: Scheduler): ScalazTask[A] = {
    import scalaz.{ \/, -\/, \/- }
    scalaz.concurrent.Task.async[A](
      register => mtask.runAsync { tr =>
        tr match {
          case Success(r) => register(\/-(r))
          case Failure(t) => register(-\/(t))
        }
      })
  }
}