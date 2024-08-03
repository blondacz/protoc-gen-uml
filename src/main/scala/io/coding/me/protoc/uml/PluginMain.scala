package io.coding.me.protoc.uml

import com.github.os72.protocjar.Protoc
import protocbridge.ProtocBridge

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

  val code = ProtocBridge.runWithGenerators(a => Protoc.runProtoc(versionFlag +: a.toArray), Seq(ProtocUMLGenerator.name -> ProtocUMLGenerator()), protocArgs)

  sys.exit(code)
}
