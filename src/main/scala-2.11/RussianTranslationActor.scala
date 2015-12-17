package com.igumnov.top3000words

import akka.actor.{ActorRef, Actor, Props}

import scala.util.{Failure, Success}

class RussianTranslationActor  (dictionaryActor: ActorRef) extends Actor {
  println("RussianTranslationActor")


  def receive = {
    case (word: String, russianPage: String) => {
      LingvoSite.parseTranslation(russianPage) match {
        case Success(translation) => {
            dictionaryActor ! RussianTranslation(word, translation)
        }
        case Failure(ex) => {
          println(ex.getMessage)
        }
      }
    }
  }



}
