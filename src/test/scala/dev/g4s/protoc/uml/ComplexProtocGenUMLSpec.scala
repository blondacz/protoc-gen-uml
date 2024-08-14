package dev.g4s.protoc.uml

import dev.g4s.protoc.uml.model.{Name, Package, TYPE_NAME_SEPARATOR, TypeIdentifier}

class ComplexProtocGenUMLSpec extends ProtocGenUMLSpec("complex", "complex") {

  it should "have all expected types" in {

    val musicPackage = Package("dev.g4s.schema.music")
    val utilPackage = Package("dev.g4s.schema.util")
    val databasePackage = Package("dev.g4s.schema.database")

    typeRepository.keys.map(_.pakkage) should contain(musicPackage)
    typeRepository.keys.map(_.pakkage) should contain(utilPackage)

    List("Date").map(Name).map(n => TypeIdentifier(utilPackage, n)).foreach { typeIdentifier =>
      typeRepository.keys should contain(typeIdentifier)
    }

    List("Album", s"Album${TYPE_NAME_SEPARATOR}Genre", s"Album${TYPE_NAME_SEPARATOR}Interpret", "Musician", "Band")
      .map(Name)
      .map(n => TypeIdentifier(musicPackage, n))
      .foreach { typeIdentifier =>
        typeRepository.keys should contain(typeIdentifier)
      }

  }
}
