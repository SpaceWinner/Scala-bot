package Bot

import scala.util.parsing.combinator.RegexParsers

class CommandParser extends RegexParsers {

  private val command = CommandImpl

  def createPoll: Parser[Command] = {
    val pollName = Parser("((" ~> """\w+""".r <~ "))" | """\w+""".r) //TODO () ACCEPT
    val anonymity = Parser("(" ~> ("yes" | "no") <~ ")")
    val continuous = Parser("(" ~> ("afterstop" | "continuous") <~ ")")
    val startTime = Parser("(" ~> """\d{2}:\d{2}:\d{2} \d{2}:\d{2}:\d{2}""".r <~ ")")
    val stopTime = startTime
    ("/create_poll" ~> "(" ~> pollName <~ ")") ~ anonymity.? ~ continuous.? ~ startTime.? ~ stopTime.? ^^ { case name ~ anon ~ cont ~ start ~ stop => Matcher.CreatePoolM(name, anon, cont, start, stop) } //TODO case ~ ACCEPT
  }

  def list: Parser[Command] = """^/list""".r ^^ { _ => Matcher.ListM() }

  def delete: Parser[Command] = "/delete_poll" ~> "(" ~>"""\d+""".r <~ ")" ^^ { d => Matcher.DeleteM(d.toInt) } //TODO separate ACCEPT
  def start: Parser[Command] = "/start_poll" ~> "(" ~> """\d+""".r <~ ")" ^^ { d => Matcher.StartM(d.toInt) }

  def stop: Parser[Command] = "/stop_poll" ~> "(" ~> """\d+""".r <~ ")" ^^ { d => Matcher.StopM(d.toInt) }

  def result: Parser[Command] = "/result" ~> "(" ~> """\d+""".r <~ ")" ^^ { d => Matcher.ResultM(d.toInt) }

  def begin: Parser[Command] = "/begin" ~> "(" ~> """\d+""".r <~ ")" ^^ { d => Matcher.BeginM(d.toInt) }

  def end: Parser[Command] = "/end" ^^ { _ => Matcher.EndM() }

  def view: Parser[Command] = "/view" ^^ { _ => Matcher.ViewM() }

  def deleteQuestion: Parser[Command] = "/delete_question" ~> "(" ~> """\d+""".r <~ ")" ^^ { d => Matcher.DeleteQuestionM(d.toInt) }

  val rlt: Parser[Command] = createPoll | list | delete | start | stop | result | begin | end | view | deleteQuestion | failure("Unknown command")


  def apply(input: String): ParseResult[Command] = parse(rlt, input)

}

object CommandParser extends CommandParser