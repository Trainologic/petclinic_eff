package com.trainologic.samples.petclinic.model

import argonaut.EncodeJson
import argonaut.Argonaut
import java.time.LocalDate
import argonaut.DecodeJson
import argonaut.CodecJson

object ArgonautCodecs {

  implicit def DateTimeAsISO8601EncodeJson: EncodeJson[LocalDate] = ???
  implicit def DatesdfTimeAsISO8601EncodeJson: DecodeJson[LocalDate] = ???

  implicit val ownerCodecJson: CodecJson[Owner] =
    Argonaut.casecodec6(Owner.apply, Owner.unapply)("id", "firstName", "lastName", "address", "city", "telephone")

  implicit val petCodecJson: CodecJson[Pet] =
    Argonaut.casecodec5(Pet.apply, Pet.unapply)("id", "name", "birthDate", "petType", "owner")

}