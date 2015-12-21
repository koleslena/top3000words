package com.igumnov.top3000words

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import TranslateObjects.{TranslationTranscription, TranslatedWord}
import scala.concurrent.{Future}
import scala.concurrent.duration._
import concurrent.ExecutionContext.Implicits.global

object Top3000WordsApp extends App with LazyLogging {

  val system = ActorSystem("Top3000Words")

  start()

  //scala.io.StdIn.readLine()
  //system.terminate()

  def start() = {

    implicit val timeout = Timeout(1000.second)

    logger.info("START")

    val listFuture = Future.traverse(OxfordSite.getTableOfContent) { name =>
      (system.actorOf(Props(new PagesActor())) ? Some(name))
    }.mapTo[List[List[List[TranslatedWord]]]]


    listFuture.map {
      res => {
        val strings: List[String] = res.flatten.flatten.map { word => word.toString}
        logger.info("size " + strings.size)

        val text = strings.mkString("\n")
        scala.tools.nsc.io.File("dictionary.txt").writeAll(text)
        logger.info("END")

      }
    }


  }

}
