package dev.g4s.protoc.uml

import com.typesafe.config.{ConfigException, ConfigFactory}
import dev.g4s.protoc.uml.config.{Config, Configuration, Output, OutputFileOrganization}
import org.scalatest.{BeforeAndAfterEach, Inside}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.error.{CannotReadFile, ConfigReaderFailures, ThrowableFailure}

import java.nio.file.Paths
import scala.util.Try

class ConfigurationSpec extends AnyFlatSpec with Matchers with Inside {

  it should "report config loading failures" in new ConfigTesting("NON_EXISTENT") {
    private val result = loadedConfig

    inside(result) {
      case Left(ConfigReaderFailures(ThrowableFailure(e, _))) =>
        e shouldBe a[ConfigException.IO]
        e.getMessage should include("resource not found on classpath: NON_EXISTENT")
    }
  }

  it should "should load from specified resource" in new ConfigTesting("test-application.conf") {
    private val result = loadedConfig

    inside(result) {
      case Right(Config(Output(_, organization, _, _, _),_)) =>
        organization shouldBe OutputFileOrganization.DIRECT_MAPPING
    }
  }
}


trait ConfigOverride extends BeforeAndAfterEach {
  self: AnyFlatSpec =>

  def configLocation: String
  override def beforeEach(): Unit = {
    sys.props.exclusively {
      ConfigFactory.invalidateCaches()
      sys.props.update("config.resource", configLocation)
    }
  }

  override def afterEach(): Unit = {
    sys.props.exclusively {
      sys.props.remove("config.resource")
    }
  }
}

abstract class ConfigTesting(path: String) {
  def loadedConfig: Either[ConfigReaderFailures, Config] = {
    sys.props.exclusively {
      ConfigFactory.invalidateCaches()
      sys.props.update("config.resource", path)
      try {
        Configuration.apply()
      } finally {
        sys.props.remove("config.resource")
      }
    }
  }
}

