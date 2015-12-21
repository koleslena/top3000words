package com.igumnov.top3000words

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class RussianTranslationActor extends Actor with LazyLogging {
  override def receive = {
    case Some(word: String) => {
      val lastSender = sender

      logger.info("russian translate start word={}", word)

      LingvoSite.getPage(word).map(tryEnglishPage => {
        tryEnglishPage match {
          case Success(englishPage) => {

            LingvoSite.parseTranslation(englishPage) match {
              case Success((translation)) => {
                 lastSender ! Some(translation)
              }
              case Failure(ex) => {
                logger.error("russian translate word={} error={}", word, ex)
                lastSender ! None
              }
            }
          }
          case Failure(ex) =>
            logger.error("russian translate word={} error={}", word, ex)
            lastSender ! None
        }
      })
      logger.info("russian translate end word={}", word)
    }
  }
}
