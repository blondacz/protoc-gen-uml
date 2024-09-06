package dev.g4s.protoc.uml

import com.github.os72.protocjar.Protoc
import com.typesafe.config.ConfigFactory
import dev.g4s.protoc.uml.PluginMain.protocArgs
import dev.g4s.protoc.uml.config.{Config, Configuration}
import dev.g4s.protoc.uml.model.TypeRepository
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import protocbridge._

import java.io.File
import java.nio.file.Files

abstract class ProtocGenUMLSpec(name: String, folder: String) extends AnyFlatSpec with Matchers with Inside {

  private val tmpFile                  = Files.createTempDirectory("compiled-protos-").toFile
  private var testDir: String          = _
  private var testFiles: Array[String] = Array()

  protected def config: Config          = {
    ConfigFactory.invalidateCaches()
    Configuration().fold(f => throw new AssertionError(f.prettyPrint()), c => c)
  }

  protected lazy val protocUMLGenerator: ProtocUMLGenerator = ProtocUMLGenerator(Some(config)).fold(f => throw new AssertionError(f.prettyPrint()), c => c)
  protected lazy val typeRepository: TypeRepository = protocUMLGenerator.typeRepository
  protected lazy val fileContent: Map[String, String] = protocUMLGenerator.fileContent

  tmpFile.deleteOnExit()

  s"The $name PB model transformer " should "load proper proto files" in {

    // Not the best way to search for files in resource directory,
    // but gets the job done when executing as part of sbt test.

    testDir = Seq(new File(".").getCanonicalPath, "src", "test", "resources", "sample-protos", folder).mkString(File.separator)

    val d = new File(testDir)

    d.exists should be(true)
    d.isDirectory should be(true)

    testFiles = d.listFiles.filterNot(_.getName.startsWith(".")).map(_.getAbsolutePath)
    testFiles.nonEmpty should be(true)
  }

  it should "compile the protos at all" in {

    val protocArgs = Array(s"--uml_out=$tmpFile", "-I", testDir) ++ testFiles
    val versionFlag = s"-v${ProtocVersion.v3}"
    val protocRunner : ProtocRunner[Int] = ProtocRunner.fromFunction((args, extraEnv) =>
      Protoc.runProtoc(versionFlag +: args.toArray))

    val code = ProtocBridge.runWithGenerators(protocRunner,Seq(ProtocUMLGenerator.name -> protocUMLGenerator),protocArgs)

    code should be(0)
  }

  it should "have a non empty type repository" in {
    protocUMLGenerator.typeRepository should not be empty
  }
}














