package com.igumnov.top3000words

case class Word (word: String, transcription: Option[String] = None, russianTranslation:Option[String] = None, englishTranslation: Option[String] = None)
case class RussianTranslation(word:String, translation: String)
case class EnglishTranslation(word:String, translation: String)
case class Transcription(word:String, transcription: String)
