package com.trainologic.samples.petclinic.model

import argonaut.EncodeJson
import argonaut.Argonaut

object ArgonautCodecs {
  
  implicit val ownerCodecJson: EncodeJson[Owner] =
    Argonaut.casecodec6(Owner.apply, Owner.unapply)("id", "firstName", "lastName", "address", "city", "telephone")

    
}