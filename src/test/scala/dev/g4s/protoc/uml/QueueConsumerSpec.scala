package dev.g4s.protoc.uml

package dev.g4s.protoc.uml

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.concurrent.Eventually

class QueueConsumerSpec extends AnyFlatSpec with Eventually {

  "QueueConsumer" should "process items when running and stop processing after stop is called" in {
    // Counter to track processed items
    val processedCount = new AtomicInteger(0)

    // Create a QueueConsumer instance
    val consumer = new QueueConsumer[String]("TestConsumer", limit = 5, stopTimeout = 1000.millis) {
      override def runOne(item: String): Unit = {
        println(s"Processing item: $item")
        processedCount.incrementAndGet()
      }
    }

    // Start the consumer
    consumer.start()

    // Enqueue some items
    consumer.enqueue("Item1")
    consumer.enqueue("Item2")
    consumer.enqueue("Item3")

    // Allow some time for items to be processed
    Thread.sleep(500)

    // Verify that items have been processed
    assert(processedCount.get() == 3)

    // Stop the consumer
    consumer.stop()

    // Enqueue more items after stopping
    consumer.enqueue("Item4")
    consumer.enqueue("Item5")

    // Allow some time to see if items are processed (they shouldn't be)
    Thread.sleep(500)

    // Verify that no additional items have been processed
    assert(processedCount.get() == 3)
  }

  it should "not process items enqueued after stopping" in {
    // Counter to track processed items
    val processedCount = new AtomicInteger(0)

    // Create a QueueConsumer instance
    val consumer = new QueueConsumer[Int]("TestConsumerInt", limit = 5, stopTimeout = 1000.millis) {
      override def runOne(item: Int): Unit = {
        println(s"Processing item: $item")
        processedCount.addAndGet(item)
      }
    }

    // Start the consumer
    consumer.start()

    // Enqueue some items
    consumer.enqueue(1)
    consumer.enqueue(2)
    consumer.enqueue(3)

    // Allow some time for items to be processed
    Thread.sleep(500)

    // Verify that items have been processed
    assert(processedCount.get() == 6)

    // Stop the consumer
    consumer.stop()

    // Enqueue more items after stopping
    consumer.enqueue(4)
    consumer.enqueue(5)

    // Allow some time to see if items are processed (they shouldn't be)
    Thread.sleep(500)

    // Verify that no additional items have been processed
    assert(processedCount.get() == 6)
  }

  it should "not process remaining items in the queue after stopping" in {
    // Counter to track processed items
    val processedCount = new AtomicInteger(0)

    // Create a QueueConsumer instance with a limit of 2 to cause blocking
    val consumer = new QueueConsumer[String]("TestConsumerLimit", limit = 2, stopTimeout = 1000.millis) {
      override def runOne(item: String): Unit = {
        println(s"Processing item: $item")
        processedCount.incrementAndGet()
        // Simulate processing time
        val x = Thread.interrupted()
        Thread.sleep(1000)
        if (x) {
          Thread.currentThread().interrupt()
        }

      }
    }

    // Start the consumer
    consumer.start()

    // Enqueue more items than the queue can hold immediately
    consumer.enqueue("Item1")
    consumer.enqueue("Item2")
    consumer.enqueue("Item3")
    consumer.enqueue("Item4")

    // Allow some time for items to be processed
    Thread.sleep(500)

    // Stop the consumer while items are still in the queue
    consumer.stop()
    consumer.enqueue("Item5")

    // Allow some time to see if remaining items are processed (they shouldn't be)
    Thread.sleep(500)

    // Verify that only some items have been processed
    assert(processedCount.get() == 4)
  }
}

