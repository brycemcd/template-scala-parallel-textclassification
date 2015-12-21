package org.template.textclassification

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vector
import com.github.fommil.netlib.F2jBLAS

import scala.math._

// FIXME: not used, but Evaluation fails without it
case class  BinRejectAlgoParams(
  lambda: Double
) extends Params

class BinRejectAlgorithm extends P2LAlgorithm[PreparedData, BinRejectModel, Query, PredictedResult] {

  // Train your model.
  def train(sc: SparkContext, pd: PreparedData): BinRejectModel = {
    new BinRejectModel
  }

  // Prediction method for trained model.
  def predict(model: BinRejectModel, query: Query): PredictedResult = {
    model.predict(query.text)
  }
}

class BinRejectModel extends Serializable {


  private val rejectWords : Array[String] = Array("unsubscribe")

  private def containsRejectWord(query : String) : Boolean = {
    val words = query.split(" ")
    val matchedIndex : Int = words.map(word => rejectWords.indexOf(word.toLowerCase)).max
    matchedIndex > -1
  }

  private val rejectPhrases : Array[String] = Array("A LERER HIPPEAU VENTURES EXPERIMENT")

  private def containsRejectPhrase(query: String) : Boolean = {
    rejectPhrases.indexOf(query.trim) > -1
  }

  private def wordCountThreshold(query : String, threshold : Integer = 4) = {
    query.split(" ").length <= threshold
  }

  private def shouldReject(query: String) : Boolean = {
    containsRejectWord(query) ||
      containsRejectPhrase(query) ||
      wordCountThreshold(query)
  }

  // TODO: if I've rejected a query before, reject it again
  def predict(query : String) : PredictedResult = {
    val str : String = shouldReject(query) match {
      case true => "reject"
      case false => "no-reject"
    }

    new PredictedResult(str, 1.0)
  }
}
