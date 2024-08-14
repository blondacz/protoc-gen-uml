package dev.g4s.protoc.uml

import dev.g4s.protoc.uml.model.FieldTypes.CompoundType
import dev.g4s.protoc.uml.model.Multiplicities.Optional
import dev.g4s.protoc.uml.model.{MessageFields, Name, Package, TYPE_NAME_SEPARATOR, TypeIdentifier, Types}

class NestedProtocGenUMLSpec extends ProtocGenUMLSpec("nested", "p3") {

  private lazy val pakkage = Package("test.package")

  private val identifier = TypeIdentifier(pakkage, Name("SampleMessage"))
  private val nestedIdentifier = TypeIdentifier(pakkage, Name(s"SampleMessage${TYPE_NAME_SEPARATOR}SubMessage"))
  private val nestedNestedIdentifier = TypeIdentifier(pakkage, Name(s"SampleMessage${TYPE_NAME_SEPARATOR}SubMessage${TYPE_NAME_SEPARATOR}SubSubMessage"))
  private val containerIdentifier = TypeIdentifier(pakkage, Name("Container"))

  it should "have nested message types" in {

    typeRepository should contain key identifier
    typeRepository should contain key nestedIdentifier
    typeRepository should contain key nestedNestedIdentifier
    typeRepository should contain key containerIdentifier
  }

  it should "have fields based on nested message types" in {

    inside(typeRepository(identifier)) {
      case Types.MessageType(id, enclosingType, fields, origin) =>
        fields should contain(MessageFields.TypedField("sub_message", CompoundType(nestedIdentifier), Some(Optional)))
        fields should contain(MessageFields.TypedField("sub_sub_message", CompoundType(nestedNestedIdentifier), Some(Optional)))
        fields should contain(MessageFields.TypedField("container", CompoundType(containerIdentifier), Some(Optional)))
    }
  }
}
