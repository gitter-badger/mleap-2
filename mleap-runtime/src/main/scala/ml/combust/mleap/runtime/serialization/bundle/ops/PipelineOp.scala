package ml.combust.mleap.runtime.serialization.bundle.ops

import ml.combust.mleap.runtime.transformer.{Pipeline, Transformer}
import ml.bundle.op.{OpModel, OpNode}
import ml.bundle.serializer.{BundleContext, GraphSerializer}
import ml.bundle.dsl._

/**
  * Created by hollinwilkins on 8/22/16.
  */
object PipelineOp extends OpNode[Pipeline, Pipeline] {
  override val Model: OpModel[Pipeline] = new OpModel[Pipeline] {
    override def opName: String = Bundle.BuiltinOps.pipeline

    override def store(context: BundleContext, model: WritableModel, obj: Pipeline): WritableModel = {
      val nodes = GraphSerializer(context).write(obj.transformers)
      model.withAttr(Attribute("nodes", Value.stringList(nodes)))
    }

    override def load(context: BundleContext, model: ReadableModel): Pipeline = {
      val nodes = GraphSerializer(context).read(model.value("nodes").getStringList).map(_.asInstanceOf[Transformer])
      Pipeline(transformers = nodes)
    }
  }

  override def name(node: Pipeline): String = node.uid

  override def model(node: Pipeline): Pipeline = node

  override def load(context: BundleContext, node: ReadableNode, model: Pipeline): Pipeline = {
    model.copy(uid = node.name)
  }

  override def shape(node: Pipeline): Shape = Shape()
}
