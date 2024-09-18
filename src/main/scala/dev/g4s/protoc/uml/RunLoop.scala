package dev.g4s.protoc.uml

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, LinkedBlockingQueue}

class QueueConsumer {
  private val queue: BlockingQueue[String] = new  ArrayBlockingQueue[String](20)
  private var consumerThread: Thread = _

  // Starts the consumer thread
  def start(): Unit = {
    if (consumerThread == null || !consumerThread.isAlive) {
      consumerThread = new Thread(new Runnable {
        override def run(): Unit = {
          try {
            while (true) {
              val item = queue.take()  // Blocks until an item is available or thread is interrupted
              println(item)
            }
          } catch {
            case _: InterruptedException =>
              Thread.sleep(1000)
            // Thread was interrupted; exit gracefully
          }
        }
      })
      consumerThread.start()
    }
  }

  // Stops the consumer thread with a timeout
  def stop(timeoutMillis: Long): Unit = {
    if (consumerThread != null && consumerThread.isAlive) {
      consumerThread.interrupt()  // Interrupts the thread if it's blocked
      consumerThread.join(timeoutMillis)
      if (consumerThread.isAlive) {
        println(s"Consumer thread did not terminate within $timeoutMillis milliseconds.")
        // Additional handling can be implemented here if needed
      }
    }
  }

  // Adds an item to the queue
  def enqueue(item: String): Unit = {
    queue.put(item)
  }
}


object QueueConsumerTest {
  def main(args: Array[String]): Unit = {
    val consumer = new QueueConsumer

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
    consumer.stop(1)
  }
}
