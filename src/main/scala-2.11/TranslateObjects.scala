package com.igumnov.top3000words

/**
 * Created by elenko on 21.12.15.
 */
object TranslateObjects {
  case class PageGroupLetter(groupLetterName: String, pageNum: Int)
  case class TranslatedWord (word: String, transcriptionEnglishTranslation: Option[TranslationTranscription], russianTranslation: Option[String]) {

    val EmptyTranslation = TranslationTranscription(Some(""), Some(""))

    override def toString = {
      val orElse: TranslationTranscription = this.transcriptionEnglishTranslation.getOrElse(EmptyTranslation)
      (this.word + "|" + orElse.transcription.getOrElse("-") + "|" + orElse.englishTranslation.getOrElse("-") + "|" + this.russianTranslation.getOrElse("-"))
    }
  }
  case class TranslationTranscription(transcription: Option[String], englishTranslation:Option[String])
}
