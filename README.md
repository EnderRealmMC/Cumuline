# Cumuline

> English documentation · [中文文档](README.zh-CN.md)

## Contents

- [Overview](#overview)
- [Packet delivery](#packet-delivery)
- [Current support](#current-support)
- [Requirements](#requirements)
- [API usage](#api-usage)
- [Building](#building)
- [Manual client test plugin](#manual-client-test-plugin)
- [License and upstream dependencies](#license-and-upstream-dependencies)

## Overview

Cumuline is an experimental Paper plugin toolkit for sending Bedrock Edition packets to Floodgate players from a Java Edition server.

It is intended for Java plugins that need Bedrock-only presentation features, such as popups, jukebox popups, and future UI experiments. Cumuline currently exposes a small API and keeps the protocol implementation private so that more packet families can be added without exposing Cloudburst Protocol types to plugin authors.

> **Experimental and unsafe**
>
> Cumuline sends raw Bedrock packet payloads through Floodgate's unsafe API. An incorrect packet ID, payload, field order, or protocol version can disconnect a client, make the client behave unexpectedly, or crash the client. Test every change with the exact Bedrock client versions used by your server before deploying it to production.

## Packet delivery

The delivery path is:

```text
Cumuline
  -> Cloudburst Protocol encodes a Bedrock packet
  -> Floodgate unsafe().sendPacket(...)
  -> floodgate:packet plugin message
  -> Geyser forwards the raw packet unchanged
  -> Bedrock client
```

Geyser does not decode and re-encode packets received through Floodgate's `floodgate:packet` channel. Cumuline therefore performs the encoding locally with Cloudburst Protocol. Geyser remains responsible for the Java-to-Bedrock connection and for forwarding the raw packet to the Bedrock client.

## Current support

The first release supports:

- server-provided `POPUP` text;
- server-provided `JUKEBOX_POPUP` text;
- client-translated `JUKEBOX_POPUP` text with a translation key and parameters;
- a deliberately unsafe raw packet API for future packet families.

The current internal codec is Cloudburst Protocol `3.0.0.Beta13-SNAPSHOT`, using codec v1001 for Bedrock `1.26.30` through `1.26.33`. This includes Bedrock `1.26.31`. The codec is used only inside Cumuline and is relocated in the production shadow JAR. Cumuline does not depend on Geyser implementation classes.

The codec version is fixed in `build.gradle`. Floodgate's unsafe API does not provide Cumuline with enough information to select a codec automatically for every client, so this release does not implement automatic multi-version selection.

## Requirements

- Paper API `1.21.4-R0.1-SNAPSHOT` for compilation;
- Java 21;
- Floodgate 2.2.x with the unsafe packet API;
- Geyser configured to accept the Bedrock client connection;
- a Bedrock client compatible with the configured Cloudburst codec.

Floodgate is a runtime dependency and is not bundled into Cumuline. Cloudburst Protocol is bundled and relocated into the production JAR. Bukkit, Floodgate, and Netty remain server-provided dependencies.

## API usage

The public API is registered through Bukkit's `ServicesManager` and can also be looked up with `CumulineApi.get()`:

```java
import cn.enderrealm.cumuline.api.CumulineApi;
import org.bukkit.entity.Player;

import java.util.List;

public void showBedrockMessage(Player player) {
    CumulineApi cumuline = CumulineApi.get();

    cumuline.sendPopup(player, "Welcome to the server");
    cumuline.sendJukeboxPopup(player, "Now playing: Ender Realm");
    cumuline.sendTranslatedJukeboxPopup(
            player,
            "record.nowPlaying",
            List.of("Artist", "Track")
    );
}
```

The translated method sends the key and parameters to the Bedrock client. The raw text methods send the server-provided text directly and do not use client-side i18n.

`sendRawPacket(Player, int, byte[])` is intentionally part of the public API so future packet families can be tested without changing the transport layer. The payload must not contain the packet ID; Floodgate adds the packet ID to its plugin message envelope. This method is experimental and unsafe, and should only be used with a verified packet definition.

When the target is offline or is not a Floodgate Bedrock player, Cumuline throws `NotBedrockPlayerException`. Floodgate delivery failures are reported as `PacketSendException`.

## Building

Cumuline is an independent Gradle project. The repository currently provides a shared Gradle installation rather than a wrapper in this small scaffold.

```text
gradle build
```

The production plugin is generated as:

```text
build/libs/Cumuline-1.0-SNAPSHOT-all.jar
```

The manual test plugin is generated separately as:

```text
build/libs/Cumuline-TestPlugin-1.0-SNAPSHOT.jar
```

Install both JARs together with Floodgate and Geyser in an isolated test server.

## Manual client test plugin

The `testPlugin` source set produces a separate plugin containing only manual test commands. It sends packets to the command executor and does not run an automatic packet loop.

Commands:

```text
/cumulinetest popup <message>
/cumulinetest jukebox-raw <message>
/cumulinetest jukebox <translation-key> [parameters...]
```

The command has the `cumuline.test` permission and defaults to operators. Java players are rejected by the Cumuline API; Bedrock players can observe the actual client rendering.

## License and upstream dependencies

Cumuline is distributed under the license in `LICENSE`. Cloudburst Protocol is an Apache-2.0 licensed upstream project and is used as an internal packet model and serializer. Floodgate remains an external runtime dependency.
