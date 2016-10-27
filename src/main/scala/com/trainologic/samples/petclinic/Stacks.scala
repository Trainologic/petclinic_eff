package com.trainologic.samples.petclinic

import org.atnos.eff.Fx
import cats.data.Xor
import org.atnos.eff.Validate

object Stacks {
  type BasicStack = Fx.fx2[DataAccessException Xor ?, Validate[String, ?]] 
}