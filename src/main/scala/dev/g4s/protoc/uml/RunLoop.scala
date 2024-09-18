package dev.g4s.protoc.uml

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, LinkedBlockingQueue}
import scala.concurrent.duration.{DurationInt, FiniteDuration}

abstract class QueueConsumer[A](name: String, limit: Int, stopTimeout: FiniteDuration) {
  private val queue: BlockingQueue[A] = new  ArrayBlockingQueue[A](limit)
  private var consumerThread: Thread = _

  // Starts the consumer thread
  def start(): Unit = {
    println(s"Starting $name consumer ...")
    if (consumerThread == null || !consumerThread.isAlive) {
      consumerThread = new Thread(() => {
        try {
          while (true) {
            val item = queue.take() // Blocks until an item is available or thread is interrupted
            runOne(item)
          }
        } catch {
          case _: InterruptedException =>
            Thread.sleep(1000)
          // Thread was interrupted; exit gracefully
        }
      })
      consumerThread.setName(name)
      consumerThread.start()
      println(s"$name consumer started")
    }
  }

  def runOne(item: A) : Unit

  // Stops the consumer thread with a timeout
  def stop(): Unit = {
    if (consumerThread != null && consumerThread.isAlive) {
      println(s"Stopping Consumer $name with timeout $stopTimeout.")
      consumerThread.interrupt()  // Interrupts the thread if it's blocked
      consumerThread.join(stopTimeout.toMillis)
      if (consumerThread.isAlive) {
        println(s"Consumer $name thread did not terminate within $stopTimeout.")
        // Additional handling can be implemented here if needed
      } else {
        println(s"Consumer $name stopped successfully.")
      }
    }
  }

  // Adds an item to the queue
  def enqueue(item: A): Unit = {
    queue.put(item)
  }
}


object QueueConsumerTest {
  def main(args: Array[String]): Unit = {
    val consumer = new QueueConsumer[String]("RL1",2,1.millis) {
      override def runOne(item: String): Unit = println(item)
    }

    // Start the consumer thread
    consumer.start()

    // Simulate a producer adding items
    new Thread(new Runnable {
      override def run(): Unit = {
        val items = Array("Hello", "World", "This", "Is", "Scala")
        for (item <- items) {
          Thread.sleep(500)  // Simulate delay
          consumer.enqueue(item)
        }
      }
    }).start()

    // Let the consumer run for a while
    Thread.sleep(3000)

    // Stop the consumer thread with a timeout of 1000 milliseconds
    consumer.stop()
  }
}
