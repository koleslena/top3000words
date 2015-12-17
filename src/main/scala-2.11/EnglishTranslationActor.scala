package com.igumnov.top3000words

import akka.actor.{ActorRef, Actor, Props}

import scala.util.{Success, Failure}
import concurrent.ExecutionContext.Implicits.global

class EnglishTranslationActor (dictionaryActor: ActorRef) extends Actor {
  println("EnglishTranslationActor")


  def receive = {
    case (word: String, englishPage: String) => {
      OxfordSite.parseTranslation(englishPage) match {
        case Success((transcription, translation)) => {
          dictionaryActor ! EnglishTranslation(word,translation)
          dictionaryActor ! Transcription(word,transcription)
        }
        case Failure(ex) => {
          println(ex.getMessage)
        }
      }
    }
  }

}