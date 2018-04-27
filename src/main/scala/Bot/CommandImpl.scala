package Bot

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.immutable

trait Repository {
  private var polls: Map[Int, Poll] = immutable.Map[Int, Poll]()
  def putInRep(id:Int, poll: Poll) { polls = polls + (id -> poll)}
  def getRep: Map[Int, Poll] = polls
  def removeFromRep(id:Int){polls = polls - id}
  def cleanRep() {polls = polls.empty}

  def search(id: Int): Poll = {
    polls(id)
  }

  def searchOption(id:Int): Option[Poll] = {
    polls.get(id)
  }
}

object CommandImpl extends Repository {


  val formatDate = new SimpleDateFormat("hh:mm:ss yy:MM:dd")


  def getMaxID:Int = {
    getRep.lastOption.map { d =>
      d._1+1
    }.getOrElse(0)
  }


  def worker(string: String): Unit = {
    println(string)
  }

  def startTime(time:Option[String]): Option[Date] = {

    if (time.isDefined){
      return Option(formatDate.parse(time.getOrElse(formatDate.format(new Date))))
    }

    None
  }

  def getTimeFromFormat(string: String): Date = {
    formatDate.parse(string)
  }

  def stopTime(time:Option[String]): Option[Date] = {
    if (time.isDefined){
      return Option(formatDate.parse(time.getOrElse(formatDate.format(new Date))))
    }
    None
  }

  def createPoll(name: String, anonymityVar: Option[String], continuousOrAfterstopVar: Option[String],
                 startTimeVar: Option[String], stopTimeVar: Option[String]): Int = {
    val anonymity = anonymityVar.getOrElse("yes") == "yes"

    val continuousOrAfterstop = continuousOrAfterstopVar.getOrElse("afterstop") == "continuous"

    val startTime1 = startTime(startTimeVar)

    val stopTime1 = stopTime(stopTimeVar)

    val id = getMaxID

    putInRep(id,Poll(name, id, anonymity, continuousOrAfterstop, startTime1, stopTime1))


    id
  }

  def listPolls(): String = {
    getRep.aggregate("Current polls: \n")((s, p)  => s"$s ${p._1} :   ${p._2.name}\n", _ + _)
  }

  def deletePoll(id: Int): String = {

    getRep.get(id).map { (_) =>
      removeFromRep(id)
      "Poll deleted successfully"
    }.getOrElse("Error: poll is not exist")

  }

  def startPoll(id: Int, date: Date): String = {
    searchOption(id).map { poll =>

      if(PollCommand.active(poll,date)){
        return "Уже запущен"
      }

      if(poll.start_time.isDefined) return "Уже запущен"

      if(poll.start_time.isEmpty){
        putInRep(id, PollCommand.start(poll, date))
        return "The poll is started successfully"
      }
      return "Error"

    }.getOrElse("Error : poll is not exist")

  }


  def stopPoll(id: Int, date: Date): String = {

    searchOption(id).map { poll =>

      if (!PollCommand.active(poll, date)){
        return "Опрос еще не запущен"
      }
      if(poll.end_time.isEmpty){
        putInRep(id, PollCommand.stop(poll, date))
        return "The poll is stopped successfully"
      }
      else { return "Error: опрос остановится автоматически"}
      putInRep(id, PollCommand.stop(poll, date))
      return "The poll is stopped successfully"

    }.getOrElse("Error: poll is not exist")

  }


  def pollResult(id: Int): String = {
    getRep.get(id).map { poll =>
      PollCommand.getResult(search(id), new Date)
    }.getOrElse("Error: poll is not exist")
  }

}
