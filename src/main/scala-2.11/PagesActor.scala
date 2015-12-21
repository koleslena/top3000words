package com.igumnov.top3000words

import akka.actor.{Actor, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.igumnov.top3000words.TranslateObjects.{PageGroupLetter, TranslatedWord}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Created by elenko on 21.12.15.
 */
class PagesActor extends Actor {
  implicit val timeout = Timeout(1000.second)
  override def receive = {
    case Some(name: String) => {
      val lastSender = sender

      val eventualWords = Future.traverse(1 to 6 toList) { p =>
        (context.actorOf(Props(new ReadPageActor())) ? PageGroupLetter(name, p))
      }.mapTo[List[List[TranslatedWord]]]

      eventualWords pipeTo lastSender
    }
  }
}
