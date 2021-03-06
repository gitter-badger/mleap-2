package ml.combust.mleap.core.classification

import ml.combust.mleap.core.tree.{DecisionTree, Node}
import org.apache.spark.ml.linalg.Vector

/** Class for decision tree classification models.
  *
  * @param rootNode root decision tree node
  * @param numFeatures number of features used in prediction
  * @param numClasses number of predictable classes
  */
case class DecisionTreeClassifierModel(override val rootNode: Node,
                                       numFeatures: Int,
                                       override val numClasses: Int)
  extends MultinomialClassificationModel with DecisionTree with Serializable {
  override def predictRaw(features: Vector): Vector = {
    rootNode.predictImpl(features).impurities.get
  }

  override def rawToProbabilityInPlace(raw: Vector): Vector = raw
}
