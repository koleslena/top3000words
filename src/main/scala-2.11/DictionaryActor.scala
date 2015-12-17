package com.igumnov.top3000words

import akka.actor.Actor

class DictionaryActor extends Actor {
  println("DictionaryActor")


  override def postStop(): Unit = {
    println("DictionaryActor postStop")
    val fileText = DictionaryActor.words.map{case (_, someWord)=> {
      val transcription = someWord.transcription.getOrElse(" ")
      val russianTranslation = someWord.russianTranslation.getOrElse(" ")
      val englishTranslation = someWord.englishTranslation.getOrElse(" ")
      List(someWord.word, transcription , russianTranslation , englishTranslation).mkString("|")
    }}.mkString("\n")
    scala.tools.nsc.io.File("dictionary.txt").writeAll(fileText)
    println("dictionary.txt saved")
    System.exit(0)

  }

  def receive = {
    case Transcription(wordName, transcription) => {
      val newElement = DictionaryActor.words.get(wordName) match {
        case Some(word) => word.copy(transcription = Some(transcription))
        case None =>  Word(wordName,transcription = Some(transcription))
      }
      DictionaryActor.words += wordName -> newElement
      println(newElement)
    }
    case RussianTranslation(wordName, translation) => {
      val newElement = DictionaryActor.words.get(wordName) match {
        case Some(word) => word.copy(russianTranslation = Some(translation))
        case None =>  Word(wordName,russianTranslation = Some(translation))
      }
      DictionaryActor.words += wordName -> newElement
      println(newElement)
    }
    case EnglishTranslation(wordName, translation) => {
      val newElement = DictionaryActor.words.get(wordName) match {
        case Some(word) => word.copy(englishTranslation = Some(translation))
        case None =>  Word(wordName,englishTranslation = Some(translation))
      }
      DictionaryActor.words += wordName -> newElement
      println(newElement)
    }
  }
}


object DictionaryActor {
  var words = scala.collection.mutable.Map[String, Word]()
}