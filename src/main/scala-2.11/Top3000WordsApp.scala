package com.igumnov.top3000words

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success
import scala.util.Failure

object Top3000WordsApp extends App {


  val system = ActorSystem("Top3000Words")
  val dictionatyActor = system.actorOf(Props[DictionaryActor], "dictionatyActor")
  val englishTranslationActor = system.actorOf(Props(classOf[EnglishTranslationActor], dictionatyActor), "englishTranslationActor")
  val russianTranslationActor = system.actorOf(Props(classOf[RussianTranslationActor], dictionatyActor), "russianTranslationActor")
  val mapGetPageThreadExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))
  val mapGetWordsThreadExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))


  start()

  scala.io.StdIn.readLine()
  system.terminate()
  def start() = {

    import concurrent.ExecutionContext.Implicits.global

    Future {
      OxfordSite.getTableOfContent.par.foreach(letterGroup => {
        getWords(letterGroup, 1)
      })

    }
  }


  def getWords(letterGroup: String, pageNum: Int): Unit = {
    implicit val executor = mapGetWordsThreadExecutionContext

    OxfordSite.getWordsFromPage(letterGroup, pageNum).map(tryWords => {
      tryWords match {
        case Success(Some(words)) => words.par.foreach(word => {
            parse(word,letterGroup,pageNum)
        })
        case Success(None) => Unit
        case Failure(ex) => println(ex.getMessage)
      }
    })

  }


  def parse(word: String, letterGroup: String, pageNum: Int)= {

    implicit val executor = mapGetPageThreadExecutionContext
    OxfordSite.getPage(word).map(tryEnglishPage => {
      tryEnglishPage match {
        case Success(englishPage) => {
          englishTranslationActor ! (word, englishPage)
          getWords(letterGroup, pageNum + 1)
        }
        case Failure(ex) => println(ex.getMessage)
      }
    })
    LingvoSite.getPage(word).map(_ match {
      case Success(russianPage) => {
        russianTranslationActor !(word, russianPage)
      }
      case Failure(ex) => println(ex.getMessage)
    })
  }

}
