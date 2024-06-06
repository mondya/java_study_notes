# WebSocket

WebSocket是一种在基于TCP连接上进行全双工通行的协议

全双工（Full Deuplex）：允许数据在两个方向上同时传输

半双工（Half Deuplex）：允许数据在两个方向上传输，但是同一个时间段内只允许一个方向上传输。

![image-20240319203407822](https://gitee.com/cnuto/images/raw/master/image/image-20240319203407822.png)

## 客户端（浏览器）

### 对象创建

```js
let ws = new WebSocket(URL)
```

格式：协议://ip地址/访问路径

协议：协议名称为ws

### 对象相关事件

| 事件    | 事件处理程序 | 描述                               |
| ------- | ------------ | ---------------------------------- |
| open    | ws.onopen    | 连接建立时触发                     |
| message | ws.onmessage | 客户端接收到服务器发送的数据时触发 |
| close   | ws.onclose   | 连接关闭时触发                     |

### 方法

send()：通过websocket对象调用该方法发送数据到服务器端

### 代码示例

![image-20240319204314827](C:\Users\19242\AppData\Roaming\Typora\typora-user-images\image-20240319204314827.png)

## 服务端

Tomcat的7.0.5版本开始支持WebSocket，并且实现了Java WebSocket规范。

Java WebSocket应用由一系列的Endpoint组成，endpoint是一个Java对象，代表WebSocket连接的一端，对于服务端，我们可以视为处理具体WebSocket消息的接口。

定义Endpoint：

- 编程式：继承类javax.websocket.Endpoint并实现其方法
- 注解式：定义一个类，并添加@ServerEndpoint相关注解

Endpoint实例在WebSocket握手时创建，并在客户端与服务端链接过程中有效，最后在链接关闭时结束。在Endpoint接口中明确定义了与其生命周期相关的方法，规范实现者确保生命周期的各个阶段调用实例的相关方法。生命周期方法如下：

- onOpen()（注解@OnOpen）：当开启一个新的会话时调用，该方法是客户端与服务端握手成功后调用的方法
- onClose()（注解@OnClose）：当会话关闭时调用
- onError()（注解@OnError）：当连接过程异常时调用

### 接收客户端发送的数据

- 通过添加MessageHandler消息处理器来接收消息
- 在定义Endpoint时，通过@OnMessage注解指令接收消息的方法

### 推送数据到客户端

发送消息由RemoteEndpoint完成，其实例由Session维护

- 通过session.getBasicRemote获取同步消息发送的实例，然后调用其send方法发送消息
- 通过session.getAsyncRemote获取异步消息发送实例，调用send方法发送消息

### 代码示例

![image-20240319210327836](https://gitee.com/cnuto/images/raw/master/image/image-20240319210327836.png)

## 在线聊天

流程分析

![image-20240319210825980](https://gitee.com/cnuto/images/raw/master/image/image-20240319210825980.png)

### 依赖

```gradle
implementation 'org.springframework.boot:spring-boot-starter-websocket'
```

### 配置类

```java
@Configuration
public class WebsocketConfig {
    
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

### 获取HttpSession对象

```java
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
```

### 创建Endpoint

```java
package com.xhh.smalldemobackend.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xhh.smalldemobackend.config.GetHttpSessionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
public class ChatEndpoint {
    
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    
    private HttpSession httpSession;

    /**
     * 建立websocket连接后调用
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // 将session进行保存
        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
        // 登录时需要把用户id保存到session中
        String userId = (String) this.httpSession.getAttribute("userId");
        sessionMap.put(userId, session);
        
        // 广播消息，需要将登录的所有的用户推送给所有用户
        broad2AllUsers(String.format("%s is online", userId));
    }
    
    
    private void broad2AllUsers(String message) {
        // 遍历sessionMap，将消息发送给所有用户
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            Session session = entry.getValue();
            try {
                session.getBasicRemote().sendText(message); 
            }catch (Exception e) {
                log.error("发送消息失败", e);
            }
        }
    }

    /**
     * 浏览器发送消息
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        String toUserId = jsonObject.getString("toUserId");
        String content = jsonObject.getString("content");

        // 获取接收方的session对象
        Session session = sessionMap.get(toUserId);
        
        try {
            session.getBasicRemote().sendText(content);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }

    /**
     * 断开websocket调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        // 移除当前登录的用户
        String userId = (String) this.httpSession.getAttribute("userId");
        sessionMap.remove(userId);
        // 广播消息，需要将登录的所有的用户推送给所有用户
        broad2AllUsers(String.format("%s is offline", userId));
    }
    
    
}
```

