package dev.g4s.protoc.uml

import dev.g4s.protoc.uml.model.FieldTypes.{CompoundType, ScalarValueType}
import dev.g4s.protoc.uml.model.Multiplicities.{Optional, Required}
import dev.g4s.protoc.uml.model.{MessageFields, Name, Package, TypeIdentifier, Types}

class PolymorphismProtocGenUMLSpec extends ProtocGenUMLSpec("polymorphism", "p4") {

  private lazy val pakkage = Package("test.package")

  private val personIdentifier = TypeIdentifier(pakkage, Name("Person"))
  private val addressIdentifier = TypeIdentifier(pakkage, Name("Person::Address"))
  private val musicianIdentifier = TypeIdentifier(pakkage, Name("Musician"))

  it should "have a message types" in {

    typeRepository should contain key personIdentifier
    typeRepository should contain key addressIdentifier
    typeRepository should contain key musicianIdentifier

  }

  it should "support simple extensions" in {

    inside(typeRepository(musicianIdentifier)) {
      case Types.MessageType(id, enclosingType, fields, origin) =>
        fields should not be empty
        fields should contain(MessageFields.TypedField("instrument", ScalarValueType("String"), Some(Required)))
        fields should contain(MessageFields.TypedField("number_of_albums", ScalarValueType("Int"), Some(Optional)))
        fields should contain(MessageFields.TypedField("person", CompoundType(personIdentifier), Some(Optional)))

    }
  }
}
