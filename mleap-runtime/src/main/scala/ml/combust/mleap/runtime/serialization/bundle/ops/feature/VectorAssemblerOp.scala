package ml.combust.mleap.runtime.serialization.bundle.ops.feature

import ml.combust.mleap.core.feature.VectorAssemblerModel
import ml.combust.mleap.runtime.transformer.feature.VectorAssembler
import ml.bundle.op.{OpModel, OpNode}
import ml.bundle.serializer.BundleContext
import ml.bundle.dsl._

/**
  * Created by hollinwilkins on 8/22/16.
  */
object VectorAssemblerOp extends OpNode[VectorAssembler, VectorAssemblerModel] {
  override val Model: OpModel[VectorAssemblerModel] = new OpModel[VectorAssemblerModel] {
    override def opName: String = Bundle.BuiltinOps.feature.vector_assembler

    override def store(context: BundleContext, model: WritableModel, obj: VectorAssemblerModel): WritableModel = { model }

    override def load(context: BundleContext, model: ReadableModel): VectorAssemblerModel = VectorAssemblerModel.default
  }

  override def name(node: VectorAssembler): String = node.uid

  override def model(node: VectorAssembler): VectorAssemblerModel = VectorAssemblerModel.default

  override def load(context: BundleContext, node: ReadableNode, model: VectorAssemblerModel): VectorAssembler = {
    VectorAssembler(uid = node.name,
      inputCols = node.shape.inputs.map(_.name).toArray,
      outputCol = node.shape.standardOutput.name)
  }

  override def shape(node: VectorAssembler): Shape = {
    val s = Shape()
    var i = 0
    for(input <- node.inputCols) {
      s.withInput(input, s"input$i")
       i = i + 1
    }
    s.withStandardOutput(node.outputCol)
  }
}
