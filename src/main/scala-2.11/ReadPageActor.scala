package com.igumnov.top3000words

import akka.actor.{Actor, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.igumnov.top3000words.TranslateObjects.{TranslatedWord, PageGroupLetter}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Created by elenko on 19.12.15.
 */
class ReadPageActor extends Actor with LazyLogging {
  implicit val timeout = Timeout(1000.second)
  override def receive = {
    case PageGroupLetter(groupLetterName, pageNum) => {

      val lastSender = sender

      OxfordSite.getWordsFromPage(groupLetterName, pageNum).map(tryWords => {
        tryWords match {
          case Success(Some(words)) => {

            val eventualMaybeWord1s: Future[List[TranslatedWord]] = Future.traverse(words) { word =>
                  (context.actorOf(Props(new WordActor())) ? Some(word))
            }.mapTo[List[TranslatedWord]]

            eventualMaybeWord1s pipeTo lastSender

          }
          case Success(None) =>
            defaultRes pipeTo lastSender
          case Failure(ex) =>
            logger.error("get page groupName={} page={} error={}", groupLetterName, pageNum.toString, ex)
            defaultRes pipeTo lastSender
        }
      })
    }

  }

  val defaultRes = Future {
    Nil
  }

}
