package com.igumnov.top3000words

import akka.actor.{Actor, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.igumnov.top3000words.TranslateObjects.{TranslationTranscription, TranslatedWord}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Created by elenko on 21.12.15.
 */
class WordActor extends Actor {

  implicit val timeout = Timeout(1000.second)

  override def receive = {
    case Some(word: String) =>
      val lastSender = sender

      val eventualWord1: Future[TranslatedWord] = for {
        x <- (context.actorOf(Props(new EnglishTranslationActor())) ? Some(word)).mapTo[Option[TranslationTranscription]]
        d <- (context.actorOf(Props(new RussianTranslationActor())) ? Some(word)).mapTo[Option[String]]
      } yield TranslatedWord(word, x, d)

      eventualWord1 pipeTo lastSender

  }
}
