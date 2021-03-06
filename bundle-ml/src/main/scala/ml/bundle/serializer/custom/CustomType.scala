package ml.bundle.serializer.custom

import com.trueaccord.scalapb.{GeneratedMessage, GeneratedMessageCompanion}
import ml.bundle.serializer.{BundleContext, ConcreteSerializationFormat, HasConcreteSerializationFormat, SerializationFormat}
import spray.json._

/** Type class for a custom object.
  *
  * Custom objects can be serialized in an [[ml.bundle.dsl.AttributeList]] as
  * an [[ml.bundle.dsl.Value]]. They must provide a JSON serialization as well
  * as a compact byte representation.
  *
  * @tparam T Scala class of custom type
  */
trait CustomType[T] {
  /** Get name of the custom type.
    *
    * This can be anything as long as it is unique among custom types
    *
    * @return name of the custom type
    */
  def name: String

  /** Get the Spray JSON format for the custom type.
    *
    * This is the formatter used for serializing/deserializing the custom type
    * with JSON.
    *
    * @return Spray JSON format
    */
  def format: RootJsonFormat[T]

  /** Whether or not this object is small when serialized.
    *
    * @param obj custom object
    * @return true if small byte size when serialized, false otherwise
    */
  def isSmall(obj: T): Boolean = false

  /** Whether or not this object is large when serialized.
    *
    * @param obj custom object
    * @return true if large byte size when serialized, false otherwise
    */
  def isLarge(obj: T): Boolean = !isSmall(obj)

  /** Convert object to byte array of JSON.
    *
    * @param obj custom object
    * @return byte array of JSON
    */
  def toJsonBytes(obj: T): Array[Byte] = format.write(obj).compactPrint.getBytes

  /** Convert byte array of JSON to custom object.
    *
    * @param bytes JSON byte array
    * @return deserialized custom object
    */
  def fromJsonBytes(bytes: Array[Byte]): T = format.read(new String(bytes).parseJson)

  /** Convert custom object to compact byte array.
    *
    * @param obj custom object
    * @return compact byte array serialization of obj
    */
  def toBytes(obj: T): Array[Byte]

  /** Convert compact bytes to custom object.
    *
    * @param bytes compact byte array
    * @return deserialized custom object
    */
  def fromBytes(bytes: Array[Byte]): T

  /** Get custom object serializer specific to implicit [[ml.bundle.serializer.ConcreteSerializationFormat]].
    *
    * @param format concrete serialization format determines which serializer to return
    * @return custom object serializer for given concrete format
    */
  def formatSerializer(implicit format: HasConcreteSerializationFormat): CustomSerializer[T] = format.concrete match {
    case SerializationFormat.Json => jsonSerializer
    case SerializationFormat.Protobuf => bytesSerializer
  }

  /** Get compact byte serializer.
    *
    * @return compact byte serializer for custom object
    */
  def bytesSerializer: CustomBytesSerializer[T] = CustomBytesSerializer[T](this)

  /** Get JSON serializer.
    *
    * @return JSON serialize for custom object
    */
  def jsonSerializer: CustomJsonSerializer[T] = CustomJsonSerializer[T](this)
}
