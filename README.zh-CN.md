# Cumuline

> [English documentation](README.md) · 中文文档

## 目录

- [项目简介](#项目简介)
- [数据包发送链路](#数据包发送链路)
- [当前支持](#当前支持)
- [运行要求](#运行要求)
- [API 使用](#api-使用)
- [构建](#构建)
- [手工客户端测试插件](#手工客户端测试插件)
- [许可证和上游依赖](#许可证和上游依赖)

## 项目简介

Cumuline 是一个面向 Paper 的实验性工具包，用于在 Java 版服务器中通过 Floodgate 向基岩版玩家发送基岩版数据包。

它适用于需要基岩版独有表现能力的 Java 插件，例如 Popup、JukeboxPopup，以及未来的 UI 实验功能。Cumuline 目前只暴露较小的公共 API，并将协议实现隐藏在内部，从而可以在不向插件作者暴露 Cloudburst Protocol 类型的情况下继续添加更多数据包类型。

> **实验性和不安全**
>
> Cumuline 会通过 Floodgate 的 unsafe API 发送原始基岩版数据包。错误的数据包 ID、payload、字段顺序或协议版本可能导致客户端断开、表现异常，甚至导致客户端崩溃。任何修改都必须先使用服务器实际支持的 Bedrock 客户端版本进行测试，确认后才能用于生产环境。

## 数据包发送链路

发送链路如下：

```text
Cumuline
  -> 使用 Cloudburst Protocol 编码基岩版数据包
  -> Floodgate unsafe().sendPacket(...)
  -> floodgate:packet 插件消息
  -> Geyser 原样转发原始数据包
  -> 基岩版客户端
```

Geyser 不会解码并重新编码通过 Floodgate `floodgate:packet` 通道收到的数据包。因此编码工作由 Cumuline 使用 Cloudburst Protocol 在本地完成。Geyser 仍然负责 Java 版到基岩版的连接，以及把原始数据包转发到基岩版客户端。

## 当前支持

首版支持：

- 服务器直接提供文本的 `POPUP`；
- 服务器直接提供文本的 `JUKEBOX_POPUP`；
- 带翻译键和参数、由客户端本地化的 `JUKEBOX_POPUP`；
- 为未来数据包类型保留的实验性原始数据包 API。

当前内部使用 Cloudburst Protocol `3.0.0.Beta13-SNAPSHOT`。Cumuline 会读取 `FloodgatePlayer#getVersion()`，通过独立的 `cn.enderrealm:bedrock-protocol-mappings` 映射库查找客户端协议版本，再动态加载对应的 Cloudburst codec。映射库会被打包进生产 Shadow JAR，Cloudburst 包会被重定位。Cumuline 不依赖 Geyser 的实现类。

未知客户端版本，或者当前 Cloudburst 依赖中不存在对应 codec 的版本，都会被明确拒绝；Cumuline 不会静默回退到最新 codec。映射数据和公共 API 维护在独立的 [EnderRealmMC/bedrock-protocol-mappings](https://github.com/EnderRealmMC/bedrock-protocol-mappings) 仓库中。

## 运行要求

- 编译使用 Paper API `1.21.4-R0.1-SNAPSHOT`；
- Java 21；
- 带有 unsafe 数据包 API 的 Floodgate 2.2.x；
- 已正确配置并接受基岩版连接的 Geyser；
- 当前映射表中存在、且 Cloudburst 依赖包含对应 codec 的基岩版客户端。

Floodgate 是运行时依赖，不会被打包进 Cumuline。Cloudburst Protocol 会被打包并重定位到生产 JAR 中。Bukkit、Floodgate 和 Netty 仍由服务器提供。

## API 使用

公共 API 会通过 Bukkit 的 `ServicesManager` 注册，也可以使用 `CumulineApi.get()` 便捷获取：

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

翻译方法会把翻译键和参数发送给基岩版客户端。原始文本方法直接发送服务器提供的文本，不使用客户端 i18n。

`sendRawPacket(Player, int, byte[])` 被有意加入公共 API，使未来新增数据包类型时可以复用传输层进行测试。payload 不得包含数据包 ID；Floodgate 会在插件消息封装中添加数据包 ID。该方法是实验性且不安全的接口，只能用于已经确认过数据包定义的场景。

当目标玩家离线或不是 Floodgate 基岩版玩家时，Cumuline 会抛出 `NotBedrockPlayerException`。Floodgate 发送失败会以 `PacketSendException` 报告。

## 构建

Cumuline 是独立 Gradle 项目。没有旁边映射源码仓库时，它会从 GitHub Packages 下载 `cn.enderrealm:bedrock-protocol-mappings:1.0.0`，不要求使用者额外克隆映射仓库。

```text
gradle build
```

GitHub Packages 的 Maven/Gradle 包即使设置为公开，下载时仍需要 classic Personal Access Token，并且至少需要 `read:packages` 权限。可以不修改构建文件，直接通过参数提供凭据：

```text
gradle build -PgprUser=YOUR_GITHUB_USERNAME -PgprToken=YOUR_GITHUB_TOKEN
```

在 EnderRealm 主仓库中开发时，如果映射子模块位于 `libs/bedrock-protocol-mappings`，Cumuline 会自动通过 Gradle composite build 使用本地源码。这只是主仓库开发便利，不是 Cumuline 使用者的依赖要求。

生产插件生成位置：

```text
build/libs/Cumuline-1.0-SNAPSHOT-all.jar
```

手工测试插件单独生成在：

```text
build/libs/Cumuline-TestPlugin-1.0-SNAPSHOT.jar
```

请将两个 JAR 与 Floodgate、Geyser 一起安装到隔离的测试服务器中。

## 手工客户端测试插件

`testPlugin` source set 会生成独立插件，只包含手工测试命令。它只向命令执行者发送数据包，不会自动循环刷包。

命令：

```text
/cumulinetest popup <文本>
/cumulinetest jukebox-raw <文本>
/cumulinetest jukebox <翻译键> [参数...]
```

命令需要 `cumuline.test` 权限，默认仅管理员拥有。Java 玩家会被 Cumuline API 拒绝；基岩版玩家可以直接观察客户端的实际渲染表现。

## 许可证和上游依赖

Cumuline 遵循 `LICENSE` 中的许可证。Cloudburst Protocol 是 Apache-2.0 许可证的上游项目，用作内部数据包模型和序列化器。Floodgate 仍然是外部运行时依赖。
