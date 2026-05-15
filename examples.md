# hornstull — minimal examples

A tiny direct-style Scala 3 wrapper around [Lettuce](https://lettuce.io)
using [Ox](https://ox.softwaremill.com).

The whole "wrapper" is one helper: it creates a `RedisClient` and registers
its `close()` with the surrounding Ox scope, so cleanup happens automatically
when the scope ends.

```scala
package hornstull

import io.lettuce.core.RedisClient
import ox.{OxUnsupervised, useCloseableInScope}

def redisClient(uri: String)(using OxUnsupervised): RedisClient =
  useCloseableInScope(RedisClient.create(uri))
```

`useCloseableInScope` is Ox's resource-management primitive: it acquires the
value eagerly and arranges for `.close()` to run when the enclosing
`supervised` (or other Ox) scope exits, normally or via failure.

## 1. Connect and obtain sync commands

The Java original:

```java
RedisURI uri = RedisURI.Builder
        .redis("localhost", 6379)
        .build();

RedisClient client = RedisClient.create(uri);
StatefulRedisConnection<String, String> connection = client.connect();
RedisCommands<String, String> commands = connection.sync();
```

The Scala 3 + Ox version:

```scala
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import ox.{supervised, useCloseableInScope}
import hornstull.redisClient

supervised:
  val client: RedisClient = redisClient("redis://localhost:6379")
  val conn:   StatefulRedisConnection[String, String] = useCloseableInScope(client.connect())
  val cmds:   RedisCommands[String, String] = conn.sync()
  // ... use cmds inside this scope ...
```

Notes:

- The URI string `redis://localhost:6379` is the direct equivalent of
  `RedisURI.Builder.redis("localhost", 6379).build()`. `RedisClient.create`
  has an overload that parses it.
- Both `client` (via `RedisClient.close` → `shutdown`) and `conn` (via
  `StatefulRedisConnection.close`) are released automatically when the
  `supervised` block exits — no `try`/`finally`, no leaked resources on
  failure.

## 2. set / get

The Java original:

```java
commands.set("foo", "bar");
String result = commands.get("foo");
System.out.println(result);
```

The Scala 3 + Ox version:

```scala
import ox.{supervised, useCloseableInScope}
import hornstull.redisClient

supervised:
  val client = redisClient("redis://localhost:6379")
  val conn   = useCloseableInScope(client.connect())
  val cmds   = conn.sync()

  cmds.set("foo", "bar")
  val result: Option[String] = Option(cmds.get("foo"))
  println(result) // Some(bar)
```

`cmds.get(...)` returns a Java `String` that is `null` when the key is
missing. Wrapping it in `Option(...)` lifts that into the idiomatic
`Some(value)` / `None` you'd expect from Scala.

## Running it

Both snippets are bundled into a single `@main` in
`src/main/scala/hornstull/RedisExample.scala`. With a Redis server listening
on `localhost:6379`:

```
sbt "runMain hornstull.redisExample"
```

Expected output:

```
Some(bar)
```
