package dev.g4s.protoc.uml

import dev.g4s.protoc.uml.model.FieldTypes.{MapType, ScalarValueType}
import dev.g4s.protoc.uml.model.Multiplicities.Optional
import dev.g4s.protoc.uml.model.{MessageFields, Name, Package, TYPE_NAME_SEPARATOR, TypeIdentifier, Types}

class SimpleProtocGenUMLSpec extends ProtocGenUMLSpec("simple", "p1") {

  private lazy val pakkage = Package("test.package")

  it should "have a simple message type" in {

    val identifier = TypeIdentifier(pakkage, Name("SearchRequest"))

    typeRepository should contain key identifier
    typeRepository should not contain key(identifier.copy(name = Name(s"SearchRequest${TYPE_NAME_SEPARATOR}ProjectsEntry")))
    typeRepository should not contain key(identifier.copy(name = Name(s"SearchRequest${TYPE_NAME_SEPARATOR}ProjectsInverseEntry")))

    inside(typeRepository(identifier)) {
      case Types.MessageType(id, enclosingType, fields, origin) =>
        id should be(identifier)
        enclosingType should be(None)
        fields should not be empty

        fields should contain(MessageFields.TypedField("query", ScalarValueType("String"), Some(Optional)))
        fields should contain(MessageFields.TypedField("page_number", ScalarValueType("Int"), Some(Optional)))
        fields should contain(MessageFields.TypedField("result_per_page", ScalarValueType("Int"), Some(Optional)))
        fields should contain(MessageFields.TypedField("projects", MapType(ScalarValueType("String"), ScalarValueType("Int")), None))
        fields should contain(MessageFields.TypedField("projects_inverse", MapType(ScalarValueType("Int"), ScalarValueType("String")), None))

    }
  }

  it should "have a simple enum type" in {

    val identifier = TypeIdentifier(pakkage, Name("Corpus"))

    typeRepository should contain key identifier
    inside(typeRepository(identifier)) {
      case Types.EnumType(id, enclosingType, values, origin) =>
        id should be(identifier)
        enclosingType should be(None)
        values.map(_.name) should contain only("UNIVERSAL", "WEB", "VIDEO", "PRODUCTS", "NEWS", "LOCAL", "IMAGES")
    }
  }
}
