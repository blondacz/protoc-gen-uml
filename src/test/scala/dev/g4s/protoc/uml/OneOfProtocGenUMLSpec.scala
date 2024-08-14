package dev.g4s.protoc.uml

import dev.g4s.protoc.uml.model.FieldTypes.ScalarValueType
import dev.g4s.protoc.uml.model.Multiplicities.Optional
import dev.g4s.protoc.uml.model.{MessageFields, Name, Package, TYPE_NAME_SEPARATOR, TypeIdentifier, Types}

class OneOfProtocGenUMLSpec extends ProtocGenUMLSpec("oneOf", "p2") {

  lazy val pakkage: Package = Package("test.package")

  it should "have a simple message type with oneOf field" in {

    val identifier = TypeIdentifier(pakkage, Name("SampleMessage"))

    typeRepository should contain key identifier

    typeRepository should contain key TypeIdentifier(pakkage, Name(s"SampleMessage${TYPE_NAME_SEPARATOR}TestOneof"))
    typeRepository should contain key TypeIdentifier(pakkage, Name(s"SampleMessage${TYPE_NAME_SEPARATOR}TestOneof2"))
    inside(typeRepository(identifier)) {
      case Types.MessageType(id, enclosingType, fields, origin) =>
        id should be(identifier)
        enclosingType should be(None)
        fields should not be empty

        fields should contain(MessageFields.TypedField("other", ScalarValueType("String"), Some(Optional)))
        fields should contain(
          MessageFields.OneOfField("test_oneof", TypeIdentifier(Package("test.package"), Name(s"SampleMessage${TYPE_NAME_SEPARATOR}TestOneof")))
        )
        fields should contain(
          MessageFields.OneOfField("test_oneof2", TypeIdentifier(Package("test.package"), Name(s"SampleMessage${TYPE_NAME_SEPARATOR}TestOneof2")))
        )
    }
  }
}
