package com.trainologic.samples.petclinic.model

import java.time.LocalDate

case class Visit(date: LocalDate, description: String, pet: Pet) 
case class Owner(id: Option[Int], firstName: String, lastName: String , address: String, city: String, telephone: String)
case class Pet(id: Option[Int], name: String, birthDate: LocalDate, petType: PetType, owner: Owner)