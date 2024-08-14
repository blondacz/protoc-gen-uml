package dev.g4s.protoc.uml

import protocbridge.frontend.PluginFrontend
import pureconfig.error.ConfigReaderFailures

/** Main entry point when using compiler as protoc plugin */
object Main extends App {

  ProtocUMLGenerator().map {
    gen => PluginFrontend.runWithBytes(gen, System.in.readAllBytes())
  }.fold (f => {
    f.prettyPrint()
    sys.exit(-1)
  },System.out.write)
}
