package dev.g4s.protoc.uml

import com.github.os72.protocjar.Protoc
import protocbridge.{ProtocBridge, ProtocRunner}

/**
  * Main entry point when using compiler as sbt plugin.
  *
  *  See also https://github.com/trueaccord/ScalaPB/blob/master/scalapbc/src/main/scala/com/trueaccord/scalapb/ScalaPBC.scala.
  */
object PluginMain extends App {

  val (versionFlag, protocArgs) =
    if (args.length >= 1 && args(0).startsWith("-v"))
      (args.head, args.tail)
    else
      (s"-v${ProtocVersion.v3}", args)

  ProtocUMLGenerator().map { g =>
    val protocRunner : ProtocRunner[Int] = ProtocRunner.fromFunction((args,extraEnv) =>
      Protoc.runProtoc(versionFlag +: args.toArray))
    ProtocBridge.runWithGenerators(protocRunner,Seq(ProtocUMLGenerator.name -> g),protocArgs)
  }.fold (f => {
    f.prettyPrint()
    sys.exit(-1)
  },
    code => sys.exit(code)
  )
}
