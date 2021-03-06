package ml.combust.mleap.runtime.serialization.bundle.ops.feature

import ml.bundle.dsl._
import ml.bundle.op.{OpModel, OpNode}
import ml.bundle.serializer.BundleContext
import ml.combust.mleap.core.feature.BucketizerModel
import ml.combust.mleap.runtime.transformer.feature.Bucketizer

/**
  * Created by mikhail on 9/19/16.
  */
object BucketizerOp extends OpNode[Bucketizer, BucketizerModel]{
  override val Model: OpModel[BucketizerModel] = new OpModel[BucketizerModel] {
    override def opName: String = Bundle.BuiltinOps.feature.bucketizer

    override def store(context: BundleContext, model: WritableModel, obj: BucketizerModel): WritableModel = {
      model.withAttr(Attribute("splits", Value.doubleList(obj.splits)))
    }

    override def load(context: BundleContext, model: ReadableModel): BucketizerModel = {
      BucketizerModel(splits = model.value("splits").getDoubleList.toArray)
    }

  }

  override def name(node: Bucketizer): String = node.uid

  override def model(node: Bucketizer): BucketizerModel = node.model

  override def load(context: BundleContext, node: ReadableNode, model: BucketizerModel): Bucketizer = {
    Bucketizer(inputCol = node.shape.standardInput.name,
      outputCol = node.shape.standardOutput.name,
      model = model)
  }

  override def shape(node: Bucketizer): Shape = Shape().withStandardIO(node.inputCol, node.outputCol)
}
