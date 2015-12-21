package com.igumnov.top3000words

import akka.actor.Actor
import com.igumnov.top3000words.TranslateObjects.TranslationTranscription
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class EnglishTranslationActor extends Actor with LazyLogging {
  override def receive = {
    case Some(word: String) => {
      val lastSender = sender

      logger.info("english translate start word={}", word)

      OxfordSite.getPage(word).map(tryEnglishPage => {
        tryEnglishPage match {
          case Success(englishPage) => {

            OxfordSite.parseTranslation(englishPage) match {
              case Success((transcription, translation)) => {
               lastSender ! Some(TranslationTranscription(Some(transcription), Some(translation)))
              }
              case Failure(ex) => {
                logger.error("english translate word={} error={}", word, ex)
                lastSender ! None
              }
            }
          }
          case Failure(ex) =>
            logger.error("english translate word={} error={}", word, ex)
            lastSender ! None
        }
      })

      logger.info("english translate end word={}", word)

    }
  }
}
