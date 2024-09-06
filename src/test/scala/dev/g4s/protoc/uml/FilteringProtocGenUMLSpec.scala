package dev.g4s.protoc.uml

import dev.g4s.protoc.uml.model.{Name, Package, TypeIdentifier}

class FilteringProtocGenUMLSpec extends ProtocGenUMLSpec("filtering", "filtering") with ConfigOverride {
  override def configLocation: String = "sample-protos/filtering-application.conf"

  it should "have a simple message type but not filtered types" in {
    println(config)
    val pakkage = Package("test.package")
    val googlePakkage = Package("thirdparty.package")

    val identifier = TypeIdentifier(pakkage, Name("AMessage"))
    val struct = TypeIdentifier(googlePakkage, Name("ThirdPartyType"))

    typeRepository should contain key identifier
    typeRepository should not contain key(struct)
  }

}
