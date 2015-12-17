package com.igumnov.top3000words.test
import com.igumnov.top3000words._
import org.scalatest._
import akka.testkit.TestActorRef

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Success,Failure ,Try}


class Test extends FlatSpec with Matchers {

  "Table Of Content extractor" should "download and extract content from Oxford Site" in {
    val content:List[String] = OxfordSite.getTableOfContent
    content.size should be (10)
    content.find(_ == "A-B") should be (Some("A-B"))
    content.find(_ == "U-Z") should be (Some("U-Z"))
  }

  "Words list extractor" should "download words from page" in {
    val future: Future[Try[Option[List[String]]]] = OxfordSite.getWordsFromPage("A-B", 1)
    val wordsTry:Try[Option[List[String]]] = Await.result(future,60 seconds)
    wordsTry should be a 'success
    val words = wordsTry.get
    words.get.find(_ == "abandon") should be (Some("abandon"))

  }
  "Words list extractor" should "return None from empty page" in {
    val future: Future[Try[Option[List[String]]]] = OxfordSite.getWordsFromPage("A-B", 999)
    val wordsTry:Try[Option[List[String]]] = Await.result(future,60 seconds)
    wordsTry should be a 'success
    val words = wordsTry.get
    words should be(None)

  }

  "Russian Translation" should "download translation and parse" in {
    val page: Future[Try[String]] =  LingvoSite.getPage("test")
    val pageResultTry: Try[String]= Await.result(page,60 seconds)
    pageResultTry should be a 'success
    val pageResult = pageResultTry.get
    pageResult.contains("тест") should be(true)
    LingvoSite.parseTranslation(pageResult).get should be("тест")
  }



  "English Translation" should "download translation and parse" in {
    val page: Future[Try[String]] =  OxfordSite.getPage("test")
    val pageResultTry: Try[String] = Await.result(page,60 seconds)
    pageResultTry should be a 'success
    val pageResult = pageResultTry.get
    pageResult.contains("examination") should be(true)
    OxfordSite.parseTranslation(pageResult).get should be(("test", "an examination of somebody’s knowledge or ability, consisting of questions for them to answer or activities for them to perform"))

  }



}