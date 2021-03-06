package org.apache.spark.ml.bundle.ops.classification

import ml.bundle.op.{OpModel, OpNode}
import ml.bundle.serializer.{BundleContext, ModelSerializer}
import ml.bundle.dsl._
import org.apache.spark.ml.attribute.NominalAttribute
import org.apache.spark.ml.classification.ClassificationModel
import org.apache.spark.ml.mleap.classification.OneVsRestModel

/**
  * Created by hollinwilkins on 8/21/16.
  */
object OneVsRestOp extends OpNode[OneVsRestModel, OneVsRestModel] {
  override val Model: OpModel[OneVsRestModel] = new OpModel[OneVsRestModel] {
    override def opName: String = Bundle.BuiltinOps.classification.one_vs_rest

    override def store(context: BundleContext, model: WritableModel, obj: OneVsRestModel): WritableModel = {
      var i = 0
      for(cModel <- obj.models) {
        val name = s"model$i"
        ModelSerializer(context.bundleContext(name)).write(cModel)
        i = i + 1
        name
      }

      model.withAttr(Attribute("num_classes", Value.long(obj.models.length)))
    }

    override def load(context: BundleContext, model: ReadableModel): OneVsRestModel = {
      val numClasses = model.value("num_classes").getLong.toInt

      val models = (0 until numClasses).toArray.map {
        i => ModelSerializer(context.bundleContext(s"model$i")).read().asInstanceOf[ClassificationModel[_, _]]
      }

      val labelMetadata = NominalAttribute.defaultAttr.
        withName("prediction").
        withNumValues(models.length).
        toMetadata
      new OneVsRestModel(uid = "", models = models, labelMetadata = labelMetadata)
    }
  }

  override def name(node: OneVsRestModel): String = node.uid

  override def model(node: OneVsRestModel): OneVsRestModel = node

  override def load(context: BundleContext, node: ReadableNode, model: OneVsRestModel): OneVsRestModel = {
    val labelMetadata = NominalAttribute.defaultAttr.
      withName(node.shape.output("prediction").name).
      withNumValues(model.models.length).
      toMetadata
    val m = new OneVsRestModel(uid = node.name, models = model.models, labelMetadata = labelMetadata).
      setFeaturesCol(node.shape.input("features").name).
      setPredictionCol(node.shape.output("prediction").name)
    for(probabilityCol <- node.shape.getOutput("probability")) m.setProbabilityCol(probabilityCol.name)
    m
  }

  override def shape(node: OneVsRestModel): Shape = {
    val s = Shape().withInput(node.getFeaturesCol, "features").
      withOutput(node.getPredictionCol, "prediction")

    if(node.isDefined(node.probabilityCol)) {
      s.withOutput(node.getProbabilityCol, "probability")
    } else { s }
  }
}
