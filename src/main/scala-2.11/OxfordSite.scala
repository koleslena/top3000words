package com.igumnov.top3000words

import java.util.concurrent.{Executors, ThreadPoolExecutor}

import org.jsoup.Jsoup

import scala.io.Source
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.nodes.Element

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object OxfordSite {

  val getPageThreadExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))

  def parseTranslation(content: String): Try[(String, String)] = {
    Try {
      val browser = new Browser
      val doc = browser.parseString(content)
      val spanElement: Element = doc >> element(".phon")
      val str = Jsoup.parse(spanElement.toString).text()
      val transcription = str.stripPrefix("BrE//").stripSuffix("//").trim
      val translation = doc >> text(".def")
      (transcription,translation)
    }
  }

  def getPage(word: String): Future[Try[String]] = {
    implicit val executor = getPageThreadExecutionContext
    Future {
      Try {
        val html = Source.fromURL("http://www.oxfordlearnersdictionaries.com/definition/english/" + (word.replace(' ','-')) + "_1")
        html.mkString
      }
    }
  }

  def getWordsFromPage(letterGroup: String, pageNum: Int): Future[Try[Option[List[String]]]] = {
    import ExecutionContext.Implicits.global

    Future {
      Try {
        val html = Source.fromURL("http://www.oxfordlearnersdictionaries.com" +
          "/wordlist/english/oxford3000/Oxford3000_" + letterGroup + "/?page=" + pageNum)
        val page = html.mkString
        val browser = new Browser
        val doc = browser.parseString(page)
        val ulElement: Element = doc >> element(".wordlist-oxford3000")
        val liElements: List[Element] = ulElement >> elementList("li")
        if (liElements.size > 0) Some(liElements.map(_ >> text("a")))
        else None
      }
    }
  }

  def getTableOfContent: List[String] = {

    val html = Source.fromURL("http://www.oxfordlearnersdictionaries.com/wordlist/english/oxford3000/Oxford3000_A-B/")
    val page = html.mkString
    val browser = new Browser
    val doc = browser.parseString(page)
    val ulElement: Element = doc >> element(".hide_phone")
    val liElements: List[Element] = ulElement >> elementList("li")
    List(liElements.head >> text("span")) ++ liElements.tail.map(_ >> text("a"))
  }

}
