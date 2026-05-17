package hornstull

import io.lettuce.core.RedisClient
import ox.{OxUnsupervised, supervised, useCloseableInScope}

def redisClient(uri: String)(using OxUnsupervised): RedisClient =
  useCloseableInScope(RedisClient.create(uri))

@main def redisExample(): Unit =
  supervised:
    val client = redisClient("redis://localhost:6379")
    val conn = useCloseableInScope(client.connect())
    val cmds = conn.sync()

    cmds.set("foo", "bar")
    val result: Option[String] = Option(cmds.get("foo"))
    println(result)
