
<div align="center">

# OneBot Client

_✨ 基于java开发的 [OneBot](https://github.com/howmanybots/onebot/blob/master/README.md) 协议客户端sdk✨_

</div>
<hr>
<p align="center">
    <a href="https://github.com/cnlimiter/onebot-client/issues"><img src="https://img.shields.io/github/issues/cnlimiter/onebot-sdk?style=flat" alt="issues" /></a>
    <a href="https://maven.nova-committee.cn/#/releases/cn/evolvefield/bot/OneBot-Client"><img src="https://jitci.com/gh/cnlimiter/onebot-sdk/svg"></a>
    <a href="https://github.com/cnlimiter/onebot-client/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-GPLV3-green" alt="License"></a>
    <a href="https://github.com/howmanybots/onebot"><img src="https://img.shields.io/badge/OneBot-v11-blue?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABABAMAAABYR2ztAAAAIVBMVEUAAAAAAAADAwMHBwceHh4UFBQNDQ0ZGRkoKCgvLy8iIiLWSdWYAAAAAXRSTlMAQObYZgAAAQVJREFUSMftlM0RgjAQhV+0ATYK6i1Xb+iMd0qgBEqgBEuwBOxU2QDKsjvojQPvkJ/ZL5sXkgWrFirK4MibYUdE3OR2nEpuKz1/q8CdNxNQgthZCXYVLjyoDQftaKuniHHWRnPh2GCUetR2/9HsMAXyUT4/3UHwtQT2AggSCGKeSAsFnxBIOuAggdh3AKTL7pDuCyABcMb0aQP7aM4AnAbc/wHwA5D2wDHTTe56gIIOUA/4YYV2e1sg713PXdZJAuncdZMAGkAukU9OAn40O849+0ornPwT93rphWF0mgAbauUrEOthlX8Zu7P5A6kZyKCJy75hhw1Mgr9RAUvX7A3csGqZegEdniCx30c3agAAAABJRU5ErkJggg=="></a>
</p>
<p align="center">
    <a href="#">文档</a> | 
    <a href="#">QuickStart</a>
</p>


# QuickStart

### 使用api进行请求
```java
public class WebSocketClientTest {
    public static void sendApi(String[] args) throws Exception {
        LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();//使用队列传输数据
        ModWebSocketClient service = ConnectFactory.createWebsocketClient(new BotConfig("ws://127.0.0.1:8080"),blockingQueue);
        service.create();//创建websocket客户端
        Bot bot = service.createBot();//创建机器人实例，以调用api
        bot.sendGroupMsg(123456, MsgUtils.builder().text("123").build(), true);//发送群消息
        GroupMemberInfoResp sender = bot.getGroupMemberInfo(123456, 123456, false).getData();//获取响应的群成员信息
        System.out.println(sender.toString());//打印
    }
}
```

### 事件监听示例
```java
public class WebSocketClientTest {
    public static void eventListener(String[] args) throws Exception {
        LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();//使用队列传输数据
        ModWebSocketClient service = ConnectFactory.createWebsocketClient(new BotConfig("ws://127.0.0.1:8080"),blockingQueue);
        service.create();//创建websocket客户端
        EventDispatchers dispatchers = new EventDispatchers(blockingQueue);//创建事件分发器
        GroupMessageListener groupMessageListener = new GroupMessageListener();//自定义监听规则
        groupMessageListener.addHandler("天气", new Handler<GroupMessageEvent>() {
            @Override
            public void handle(GroupMessageEvent groupMessage) {
                System.out.println(groupMessage);

            }
        });//匹配关键字监听
        dispatchers.addListener(groupMessageListener);//注册监听
        dispatchers.addListener(new SimpleListener<PrivateMessageEvent>() {//私聊监听
            @Override
            public void onMessage(PrivateMessageEvent privateMessage) {
                System.out.println(privateMessage);
            }
        });//快速监听

        dispatchers.start(10);//线程组处理任务

    }
}
```

# Client

OneBot-Client 以 [OneBot-v11](https://github.com/howmanybots/onebot/tree/master/v11/specs) 标准协议进行开发，兼容所有支持正向WebSocket的OneBot协议客户端

| 项目地址                                                                           | 平台                                            | 核心作者           | 备注                                                                       |
|--------------------------------------------------------------------------------|-----------------------------------------------|----------------|--------------------------------------------------------------------------|
| [koishijs/koishi](https://github.com/koishijs/koishi)                          | [koishi](https://koishi.js.org/)              | shigma         |                                                                          |
| [onebot-walle/walle-q](https://github.com/onebot-walle/walle-q)                |                                               | abrahum        |                                                                          |
| [Yiwen-Chan/OneBot-YaYa](https://github.com/Yiwen-Chan/OneBot-YaYa)            | [先驱](https://www.xianqubot.com/)              | kanri          |                                                                          |
| [richardchien/coolq-http-api](https://github.com/richardchien/coolq-http-api)  | CKYU                                          | richardchien   | 可在 Mirai 平台使用 [mirai-native](https://github.com/iTXTech/mirai-native) 加载 |
| [Mrs4s/go-cqhttp](https://github.com/Mrs4s/go-cqhttp)                          | [MiraiGo](https://github.com/Mrs4s/MiraiGo)   | Mrs4s          |                                                                          |
| [yyuueexxiinngg/OneBot-Mirai](https://github.com/yyuueexxiinngg/onebot-kotlin) | [Mirai](https://github.com/mamoe/mirai)       | yyuueexxiinngg |                                                                          |
| [takayama-lily/onebot](https://github.com/takayama-lily/onebot)                | [OICQ](https://github.com/takayama-lily/oicq) | takayama       |                                                                          |


# Credits

* [OneBot](https://github.com/botuniverse/onebot)

# License

This product is licensed under the GNU General Public License version 3. The license is as published by the Free
Software Foundation published at https://www.gnu.org/licenses/gpl-3.0.html.

Alternatively, this product is licensed under the GNU Lesser General Public License version 3 for non-commercial use.
The license is as published by the Free Software Foundation published at https://www.gnu.org/licenses/lgpl-3.0.html.

Feel free to contact us if you have any questions about licensing or want to use the library in a commercial closed
source product.

# Thanks

Thanks [JetBrains](https://www.jetbrains.com/?from=Shiro) Provide Free License Support OpenSource Project

[<img src="https://mikuac.com/images/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/?from=mirai)

## Stargazers over time

[![Stargazers over time](https://starchart.cc/cnlimiter/onebot-sdk.svg)](https://starchart.cc/cnlimiter/onebot-sdk)

