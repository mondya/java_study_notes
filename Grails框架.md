# Grails框架
基于Groovy语言开发的框架
## 框架搭建(IDEA)
- new Project->Grails
- 配置gradle
- 运行项目
  - 方式一：通过Application运行，打开grails-app/init/Application,运行main方法
  - 方式二：配置grails(类似配置tomcat)

## 框架的基本结构
约定大于配置
![grails框架结构图](images/grails.jpg)

## 配置数据库
以mysql为例,由于grails框架的yml文件中默认有三个运行环境,development,test,production,以下为datasource总配置
```yml
dataSource:
    pooled: true
    jmxExport: true
#    url: jdbc:mysql://localhost:3306/db_01?useUnicode=ture&characterEncoding=utf-8
    driverClassName: com.mysql.jdbc.Driver
    username: root
    password: xhh1999.02.10
```
分配置，以development开发环境为例
```yml
environments:
    development:
        dataSource:
            dbCreate: create-drop
            url: jdbc:mysql://localhost:3306/db_01?useUnicode=true&characterEncoding=utf-8
```
- dbCreate
  - create-drop：每次重启都会清空历史数据，开发环境使用
  - update：更新数据库
  - none：对数据库不进行任何操作
  - validate：验证

## 新建domain实体类
- 新建Book类，写入字段
-   约束属性
    -   blank和nullable的区别
        -   blank用于约束string，nullable用于约束对象
        -   blank允许空字符串，nullalble允许null
```java
static constraints={
    name(blank: false,nullable: false)
    ...
}
```
- mapping(映射数据库字段)

```java
static mapping={
    table 'book'
    id column:'id'
}
```
- 不生成对应的表结构

定义domain内部类

- 生成表，不生成某些字段的表结构

```java
//static transients = ['myfield']  定义时只需要指定这个static transients 即可

class A {
String name

static transients = ["name"]

int age

}
```
这样在数据库中只会生成age字段

- 去除乐观锁的影响

grails的domain会自动添加乐观锁，即每个表记录都有一个version字段
```java
static mapping = {
    version false
}
```
- 表已经存在，并且与domain不对应

grails domain与表结构是默认按照驼峰命名法对应的，即实体类CustomerUser对应customer_user，字段PhoneNumber对应phone_number
```java
    static mapping = {
        table('phone') //表
        phoneView column : 'phone' //字段
}
```
### domain生成controller
domain创建实体类，可以手动选择创建controller和service,类中定义了一些操作数据库的方法（增删改查）
### grails框架find,get,list
查找所有域类实例：
```java
Book.findAll()
Book.getAll()
Book.list()
```
检索指定id的域类的实例
```java
Book.findById(1)
Book.get(1)
```
- getAll是get的增强版，它带有多个id并且返回List实例。列表大小与提供的id数相同;没有则返回空列表项
- get按id查找单个实例，它使用缓存。如果在二级缓存中调用过则会一直使用缓存查询
- findAll允许使用HQL查询并支持分页
- findBy..是动态查找器，不使用类的实例缓存，但是如果开启了查询缓存，则可以缓存.findBy更加智能
- list查找所有实例并支持分页
##  Controller类
controller默认映射路径为`localhost:8080/controller类前缀名/方法名`
> 作用域

- servletContext,称为application作用域，允许在整个web应用程序中共享状态
- session,允许关联某个给定用户的状态，通常使用Cookie把一个session与用户进行关联
- request,只允许存储当前的请求对象
- params,可变的请求参数map，获取传递的参数

### 呈现视图,render,redirect,repond的区别

- render
  - 界面跳转(url不变)，向网页输出数据，render之后程序会继续执行，但是后面不能使用redirect,respond(控制台报错)
    - text-要呈现的文本数据
    - builder-渲染标记时使用的构造器
    - view-渲染的视图位置，当需要跳转到其他controller页面时显示
    - template-要呈现的模板。模板通常是一个HTML文本
    - layout-用于响应的布局
    - var-要传递给模板的变量的名称，如果未指定，则默认为Groovy的默认参数it
    - bean-在渲染中使用的bean
    - model-用于渲染的模型，返回json类型数据
    - collection-用于针对集合中的每个项目呈现模板
    - contentType-响应格式
    - encoding-响应编码
    - plugin-在其中查找模板的插件
    - status-要使用的http状态编码
    - file-与响应一起发送的byte[],java.io.File或者inputStream
    - fileName-用于渲染文件时指定文件名
  - render不在最后一行返回后面需要添加return    
- redirect
  - 重定向，url发生改变。redirect之后，程序继续执行，后面不能再次使用redirect，虽然可以使用render,respond但是没有任何作用
- respond
  - 向页面返回数据。respond之后，程序会继续执行，后面可以使用render,redirect,但是respond没有作用

### render和respond区别
respond命令和render命令相比，主要是能自动根据Accept header选择输出格式
render类似与printWriter()
```groovy
    def list() {
        def books = Book.list()

        withFormat {
            html bookList: books
            json { render books as JSON }
            xml { render books as XML }
        }
    }
    //顺序会影响输出内容
```
使用==respond==，可以简化成：
```groovy
    def list() {
        respond Book.list(),formats:['json','html','xml']
    }
    //顺序会影响输出内容
```
在view里访问Model时，会把Book.list()自动解析成为bookList 
### URL Mapping
```groovy
class UrlMappings {
    static mappings = { 
    }
}
```
- 基本URL配置
```groovy
“/student”(controller:”student”, action:”list”)
```
当访问`...../student`时会转向StudentController中的list方法 
- 映射到view

```groovy
static mappings = {
    "/"(view:"/index") //映射到根目录下的index.jsp
}

static mappings = {
    "/aboutus"(controller: "site",view:"aboutUs") //映射到controller下的某个视图
}
```
- 设置controller默认action

```groovy
static defaultAction = “list”
```
- 带参数URL

```groovy
static mappings = {
    "/student/$name"(controller:"student")
}
```
`$name`,grails会将URL中的$name位置的内容当成参数放在params中
多个参数
```groovy

static mappings = {
 
“/$year/$month?/$day?”(controller:”student”, action:”list”)
```
- 映射到不同的HTTP请求

```groovy
'/test/httpmethod'{
    controller = 'testUrlMapping'
    action=[GET:'getAction',POST:'postAction']
}
```
- 通配符的使用

```groovy
static mappings = {
    "/site/*.html"(controller:"html1") //1
    "/site/$name.html"(controller:"html2")//2
    "/site/**.html"(controller:"html3")//3
    "/site/$name**.html"(controller:"html4")//4
}
```
这里配置的1和2都会将类似的/site/ex.html转发到指定的controller中，不同的是我们可以在2的controller中通过params.name获取参数。3和4中是两级通配符，类似/site/eeee/ex.html的URL都会转发到controller中

```java
package hiiadmin.message

import com.alibaba.fastjson.JSON
import com.bugu.BaseResult
import com.bugu.ResultVO
import grails.async.Promise
import grails.async.Promises
import groovy.util.logging.Slf4j
import hiiadmin.ConstantEnum
import hiiadmin.UserService
import hiiadmin.ViewDOService
import hiiadmin.exceptions.HiiAdminException
import hiiadmin.module.TeacherDTO
import hiiadmin.module.msg.MessageVO
import hiiadmin.msg.MessageService
import hiiadmin.utils.ToStringUnits
import io.swagger.annotations.Api
import timetabling.Message
import timetabling.MessageRecord


@Api(value = "校内通知", tags = "校园服务")
@Slf4j
class HiiAdminMessageController {

    UserService userService

    MessageService messageService

    ViewDOService viewDOService

    def index() {
        ResultVO resultVO = new ResultVO()
        int p = params.int("p", 1)
        int s = params.int("s", 30)
        Byte byMe = params.byte("byMe", 1 as byte)
        String searchValue = params.searchValue
        Byte publishType = params.byte("type") as Byte
        
        Long startDate = params.long("startDate") as Long
        Long endDate = params.long("endDate") as Long
        
        BaseResult<TeacherDTO> teacherBaseResult = userService.findTeacherViaRequest(request)
        TeacherDTO teacher = teacherBaseResult.result
        List<MessageVO> messageVOList = []
        int total = 0
        if (byMe == 1 as byte) {
            def map = messageService.fetchAllMessage4senderCate(
                    teacher.id, ConstantEnum.UserTypeEnum.TEACHER.type, ConstantEnum.MessageCates.EDUCATIONAL_ADMINISTRATION_NOTICE_ONLINE.cate, searchValue, publishType, startDate, endDate, p, s)
            List<Message> messageList = map.list
            total = (int)(map.total ? map.total : 0)
            messageList?.each {
                Message message ->
                    messageVOList << viewDOService.buildTeacherMessageVO(message)
            }
        } else {
            List<MessageRecord> messageRecordList = messageService.fetchAllMessageRecord4receiverCate(
                    teacher.id, ConstantEnum.UserTypeEnum.TEACHER.type, ConstantEnum.MessageCates.EDUCATIONAL_ADMINISTRATION_NOTICE_ONLINE.cate, searchValue, startDate, endDate, p, s)
            total = messageService.countMessageRecord4receiverCate(teacher.id, ConstantEnum.UserTypeEnum.TEACHER.type, ConstantEnum.MessageCates.EDUCATIONAL_ADMINISTRATION_NOTICE_ONLINE.cate, searchValue, startDate, endDate)
            messageRecordList?.each { MessageRecord messageRecord ->
                messageVOList << viewDOService.buildTeacherMessageVOV2(messageRecord)
            }
        }

        resultVO.result.put('list', messageVOList)
        resultVO.result.put('total', total)
        resultVO.status = 1 as byte
        render text: JSON.toJSONString(resultVO), contentType: 'application/json;', encoding: "UTF-8"
    }

    def show() {
        ResultVO resultVO = new ResultVO()
        BaseResult<TeacherDTO> teacherBaseResult = userService.findTeacherViaRequest(request)
        TeacherDTO teacher = teacherBaseResult.result
        Long id = params.long('id')
        Message message = messageService.fetchMessageById(id, false)
        if (message) {
            MessageVO messageVO
            MessageRecord messageRecord = messageService.fetchMessageRecordByMessageIdAndReceiverId(message?.id, teacher?.id, ConstantEnum.UserTypeEnum.TEACHER.type)
            if (messageRecord) {
                messageVO = viewDOService.buildTeacherMessageVO(message, messageRecord)
            } else {
                messageVO = viewDOService.buildTeacherMessageVO(message)
            }
            resultVO.result = messageVO
        } else {
            resultVO.result.put("status", 0)
        }
        resultVO.status = 1 as byte
        render text: JSON.toJSONString(resultVO), contentType: 'application/json;', encoding: "UTF-8"
    }

    def save() {
        ResultVO resultVO = new ResultVO()
        String title = params.title
        String content = params.content
        String url = params.url
        Integer reSend = params.int('reSend', 0)
        Long msgId = params.long('msgId')
        String teacherIds = params.teacherIds
        String pic = params.pic
        String enclosure = params.enclosure

        Byte requireConfirm = params.byte('requireConfirm', 0 as Byte)
        String json = params.json
        BaseResult<TeacherDTO> teacherBaseResult = userService.findTeacherViaRequest(request)

        TeacherDTO teacher = teacherBaseResult.result
        Message message
        //再次发送
        if (reSend == 1) {
            message = messageService.updateLastNotifyTime(teacher.schoolId, teacher.campusId, msgId)
            messageService.asyncReSendWxMessage2Teacher(msgId)
        } else {
            //获取最终接收的教师Id列表
            Set<Long> toSendMessageTeacherIdSet = ToStringUnits.idsString2LongList(teacherIds)
            int teacherCount = toSendMessageTeacherIdSet?.size() ?: 0

            //保存消息
            def messageInfo = messageService.saveMainMessage(requireConfirm, teacher, title, content, url,
                    ConstantEnum.MessageCates.EDUCATIONAL_ADMINISTRATION_NOTICE_ONLINE.cate, teacherCount, pic, enclosure, json, teacherIds)

            message = messageInfo.message

            Promise task = Promises.task {
                messageService.genMessageRecordsAndSendWxNotify2Teacher(teacher?.campusId, message, toSendMessageTeacherIdSet)
            }
            task.onError { Throwable throwable ->
                log.error("hiiAdmin teacher notice message send", throwable)
            }
        }
        resultVO.result.put('id', message?.id)
        resultVO.status = 1

        render text: JSON.toJSONString(resultVO), contentType: 'application/json', encoding: 'utf-8'
    }

    def update() {
        ResultVO resultVO = ResultVO.success()
        Long msgId = params.long('id')
//        Byte status = params.byte("status")
        Message message = messageService.fetchMessageByIdAndStatus(msgId, 1 as byte)
        //确认+已读
        if (message?.status > ConstantEnum.MessageStatus.DELETE.status) {
            BaseResult<TeacherDTO> teacherBaseResult = userService.findTeacherViaRequest(request)
            TeacherDTO teacher = teacherBaseResult.result

            MessageRecord messageRecord = messageService.fetchMessageRecordByMessageIdAndReceiverId(msgId, teacher?.id, ConstantEnum.UserTypeEnum.TEACHER.type)
            if (messageRecord) {
                if (message.requireConfirm == 1 as byte){
                    messageRecord.status = ConstantEnum.MessageStatus.CONFIRMED.status
                } else {
                    messageRecord.status = ConstantEnum.MessageStatus.READ_NOT_CONFIRMED.status
                }
                messageService.saveMessageRecord(messageRecord)
                resultVO.result.put('id', messageRecord.id)
                message.confirmedCount = message.confirmedCount + 1
                messageService.saveMessage(message)
            }
        }
        render text: JSON.toJSONString(resultVO), contentType: 'application/json;', encoding: "UTF-8"
    }

    def delete() {
        Long msgId = params.long("id")
        Long userId = request.getAttribute("userId") as Long
        Message message = messageService.fetchMessageById(msgId, false)
        if (message.senderId == userId && message.senderType == ConstantEnum.UserTypeEnum.TEACHER.type) {
            messageService.withdrawMessage(msgId)
        } else {
            throw new HiiAdminException("仅能撤回自己发布的通知信息")
        }

        render text: JSON.toJSONString(ResultVO.success([id: msgId])), contentType: 'application/json', encoding: "UTF-8"
    }
    
    def patch(){
        ResultVO resultVO = new ResultVO()
        String title = params.title
        String content = params.content
        String url = params.url
        Long msgId = params.long('id')
        String teacherIds = params.teacherIds
        String pic = params.pic
        String enclosure = params.enclosure
        Byte requireConfirm = params.byte('requireConfirm', 1 as Byte)
        String json = params.json

        BaseResult<TeacherDTO> teacherBaseResult = userService.findTeacherViaRequest(request)

        TeacherDTO teacher = teacherBaseResult.result
        
        Message message = messageService.fetchMessageById(msgId)
        
        if (message){
            //获取最终接收的教师Id列表
            Set<Long> toSendMessageTeacherIdSet = ToStringUnits.idsString2LongList(teacherIds)
            int teacherCount = toSendMessageTeacherIdSet?.size() ?: 0
            messageService.updateMessageStatus(msgId, title, content, url, teacherIds, pic, enclosure, requireConfirm, json, teacherCount)
            Message updateMessage = messageService.fetchMessageById(msgId)
            Promise task = Promises.task {
                messageService.genMessageRecordsAndSendWxNotify2Teacher(teacher?.campusId, updateMessage, toSendMessageTeacherIdSet)
            }
            task.onError { Throwable throwable ->
                log.error("hiiAdmin teacher notice message send", throwable)
            }
        }
        resultVO.result.put('id', message?.id)
        resultVO.status = 1

        render text: JSON.toJSONString(resultVO), contentType: 'application/json', encoding: 'utf-8'
    }
}

```

```java
package hiiadmin.message

import com.alibaba.fastjson.JSON
import com.bugu.ResultVO
import com.google.common.collect.Multimap
import hiiadmin.ConstantEnum
import hiiadmin.ViewDOService
import hiiadmin.module.bugu.TeacherVO
import hiiadmin.msg.MessageService
import hiiadmin.school.TeacherService
import io.swagger.annotations.Api
import timetabling.Message
import timetabling.Teacher

@Api(value = "校内通知名单", tags = "校园服务")
class HiiAdminMessageRecordController {
    
    MessageService messageService

    TeacherService teacherService

    ViewDOService viewDOService
	
    def index() {
        ResultVO resultVO = new ResultVO()
        Long msgId = params.long('msgId')
        //TODO 本处消息状态和家校通不一致，亟待处理统一
        Byte confirmedStatus = params.byte("confirmedStatus", ConstantEnum.MessageStatus.SENT.status)
        Message message = messageService.fetchMessageByIdAndStatus(msgId, 1 as byte)
        Multimap<String, Long> messageConfirmTeachersInfo = messageService.messageConfirmTeachersInfo(msgId, confirmedStatus)
        Set<Long> confirmTeacherSet = messageConfirmTeachersInfo.get(ConstantEnum.MessageConfirmEnum.CONFIRMED.key)
        Set<Long> unConfirmTeacherSet = messageConfirmTeachersInfo.get(ConstantEnum.MessageConfirmEnum.UN_CONFIRMED.key)
        List<Teacher> confirmTeacherList = teacherService.fetchAllTeacherByIdInList(confirmTeacherSet.toList())
        List<Teacher> unConfirmTeacherList = teacherService.fetchAllTeacherByIdInList(unConfirmTeacherSet.toList())
        List<TeacherVO> confirmTeacherVOList = confirmTeacherList?.collect {
            viewDOService.buildTeacherVOById4Notice(it)
        }
        confirmTeacherVOList?.sort { it.name }
        List<TeacherVO> unConfirmTeacherVOList = unConfirmTeacherList?.collect {
            viewDOService.buildTeacherVOById4Notice(it)
        }
        unConfirmTeacherVOList?.sort { it.name }
        resultVO.result.put("lastNotifyTime", message?.lastNotifyTime?.getTime())
        resultVO.result.put("confirmCount", confirmTeacherVOList?.size() ?: 0)
        resultVO.result.put("confirm", confirmTeacherVOList)
        resultVO.result.put("unConfirmCount", unConfirmTeacherVOList?.size() ?: 0)
        resultVO.result.put("unConfirm", unConfirmTeacherVOList)
        resultVO.status = 1 as byte
        render text: JSON.toJSONString(resultVO), contentType: 'application/json;', encoding: "UTF-8"
    }
}

```

```java
package hiiadmin.msg

import com.alicp.jetcache.anno.CacheRefresh
import com.alicp.jetcache.anno.CacheType
import com.alicp.jetcache.anno.Cached
import com.dingtalk.api.request.OapiImpaasMessageGetmessageRequest
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.mysql.cj.util.TimeUtil
import grails.async.Promise
import grails.async.Promises
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import hiiadmin.ConstantEnum
import hiiadmin.UserService
import hiiadmin.apiCloud.MessageApiInvoker
import hiiadmin.dingding.DingNoticeService
import hiiadmin.module.TeacherDTO
import hiiadmin.school.CampusService
import hiiadmin.school.TeacherService
import hiiadmin.utils.TimeUtils
import timetabling.Campus
import timetabling.Message
import timetabling.MessageRecord
import timetabling.Teacher
import timetabling.User
import timetabling.WechatProfile

import javax.annotation.Resource
import java.util.concurrent.TimeUnit

@Slf4j
@Transactional
class MessageService {

    UserService userService

    TeacherService teacherService

    CampusService campusService

    @Resource
    MessageApiInvoker messageApiInvoker

    DingNoticeService dingNoticeService


    Message updateLastNotifyTime(Long schoolId, Long campusId, Long msgId) {
        Message message = Message.get(msgId)
        message.campusId = campusId
        message.schoolId = schoolId
        message.lastNotifyTime = new Date()
        message.save(failOnError: true)
    }

    void asyncReSendWxMessage2Teacher(Long msgId) {
        Promise task = Promises.task {
            reSendWxMessage2Teacher(msgId)
        }
        task.onComplete {
            log.info("hiiadmin teacher notice message send msgId@${msgId}".toString())
        }
        task.onError { Throwable throwable ->
            log.error("hiiadmin teacher notice message send msgId@${msgId}".toString(), throwable)
        }
    }

    def reSendWxMessage2Teacher(Long msgId) {
        Message message = fetchMessageById(msgId, false)
        Multimap<String, Long> messageConfirmTeachersInfo = messageConfirmTeachersInfo(message.id)
        List<Teacher> teacherList = teacherService.fetchAllTeacherByIdInList(messageConfirmTeachersInfo.get(MessageConfirmEnum.UN_CONFIRMED.key).asList())
        teacherList.each { Teacher teacher ->
            MessageRecord messageRecord = MessageRecord.findByMessageIdAndReceiverIdAndReceiverType(message.id, teacher.id, UserTypeEnum.TEACHER.type)
            //如果消息记录已存在 则只推送wx
            if (messageRecord && messageRecord.receiverOpenId) {
                String result = sendMsg2Teacher(message.campusId, messageRecord, message)
                log.info("wx notify result:${result}".toString())
            }
            //如果消息记录不存在生成记录并推送wx
            else {
//                TODO 逻辑不太对
                if (messageRecord) {
                    messageRecord.status = ConstantEnum.MessageStatus.DELETE.status
                    genMessageRecordAndSendWxMessage2Teacher(message.campusId, teacher, message)
                } else {
                    genMessageRecordAndSendWxMessage2Teacher(message.campusId, teacher, message)
                }
            }

        }
    }

    void genMessageRecordAndSendWxMessage2Teacher(Long campusId, Teacher teacher, Message message) {
        MessageRecord messageRecord
        messageRecord = new MessageRecord()
        messageRecord.cate = message.cate
        messageRecord.requireConfirm = message.requireConfirm
        WechatProfile wechatProfile = userService.fetchWechatProfileByTeacherId(teacher.id)
        if (wechatProfile) {
            messageRecord.receiverWechatId = wechatProfile.id
            messageRecord.receiverOpenId = wechatProfile.openid
        }
        messageRecord.messageId = message.id
        messageRecord.receiverType = ConstantEnum.UserTypeEnum.TEACHER.type
        messageRecord.receiverId = teacher.id
        messageRecord.status = 0 as byte
        messageRecord.dateCreated = new Date()
        messageRecord.save(failOnError: true)
        if (messageRecord.receiverOpenId) {
            String result = sendMsg2Teacher(campusId, messageRecord, message)
            log.info("wx notify result:${result}".toString())
        } else {
            log.info("teacher not bind wx , teacher:${teacher.id}".toString())
        }
    }

    Multimap<String, Long> messageConfirmTeachersInfo(Long msgId, Byte confirmedStatus = ConstantEnum.MessageStatus.CONFIRMED.status) {
        Multimap<String, Long> messageConfirmTeacherIdMultimap = HashMultimap.create()
        List<MessageRecord> messageRecordList = MessageRecord.findAllByMessageId(msgId)
        messageRecordList.each { mr ->
            if (mr.status >= confirmedStatus) {
                messageConfirmTeacherIdMultimap.put(ConstantEnum.MessageConfirmEnum.CONFIRMED.key, mr.receiverId)
            }
            messageConfirmTeacherIdMultimap.put(ConstantEnum.MessageConfirmEnum.ALL.key, mr.receiverId)
        }
        messageConfirmTeacherIdMultimap.putAll(ConstantEnum.MessageConfirmEnum.UN_CONFIRMED.key,
                messageConfirmTeacherIdMultimap.get(ConstantEnum.MessageConfirmEnum.ALL.key) - messageConfirmTeacherIdMultimap.get(ConstantEnum.MessageConfirmEnum.CONFIRMED.key))
        messageConfirmTeacherIdMultimap
    }


    /**
     * 老师发送消息 校内通知
     * @param teacher
     * @param wechatProfile
     * @param title
     * @param content
     * @param url
     * @param cate
     * @param classStudentCount
     * @return
     */
    def saveMainMessage(Byte requireConfirm, TeacherDTO teacher, String title, String content, String url, Byte cate, int teacherCount, String pic, String enclosure, String json, String ids) {
        User user = userService.fetchUserByUIdAndType(teacher.id, ConstantEnum.UserTypeEnum.TEACHER.type)
        //记录消息本体
        Message message = new Message(
                schoolId: teacher.schoolId,
                campusId: teacher.campusId,
                title: title,
                lastNotifyTime: new Date(),
                requireConfirm: requireConfirm,
                content: content,
                senderId: teacher.id,
                senderType: ConstantEnum.UserTypeEnum.TEACHER.type,
                pic: pic,
                url: url,
                enclosure: enclosure,
                cate: cate,
                total: teacherCount,
                confirmedCount: 0,
                wechatId: user?.wechatId,
                publisherName: teacher.name,
                status: ConstantEnum.MessageStatus.SENT.status,
                json: json,
                ids: ids
        )
        message.save(failOnError: true, flush: true)
        [message: message]
    }

    /**
     * 根据消息本体，teacherIds发送消息保存记录
     * @param message
     * @param clazzIds
     * @return
     */
    def genMessageRecordsAndSendWxNotify2Teacher(Long campusId, Message message, Set<Long> toSendMessageTeacherIdSet) {
        if (toSendMessageTeacherIdSet?.size() > 0) {
            List<Teacher> teacherList = teacherService.fetchAllTeacherByIdInList(toSendMessageTeacherIdSet.asList())
            teacherList?.each { Teacher teacher ->
                sendMessage2Teacher(teacher, message, campusId)
            }
        }
    }

    /**
     * 发送教务通知给老师
     * @param teacher
     * @param message
     */
    void sendMessage2Teacher(Teacher teacher, Message message, Long campusId) {
        MessageRecord messageRecord = new MessageRecord()
        messageRecord.cate = message.cate
        messageRecord.requireConfirm = message.requireConfirm
        messageRecord.receiverId = teacher.id
        WechatProfile wechatProfile = userService.fetchWechatProfileByTeacherId(teacher.id)
        if (wechatProfile) {
            messageRecord.receiverWechatId = wechatProfile.id
            messageRecord.receiverOpenId = wechatProfile.openid
        }
        messageRecord.messageId = message.id
        messageRecord.receiverType = ConstantEnum.UserTypeEnum.TEACHER.type
        messageRecord.status = 0 as byte
        messageRecord.dateCreated = new Date()
        messageRecord.lastUpdated = new Date()
        messageRecord.save(failOnError: true)
        String result = sendMsg2Teacher(campusId, messageRecord, message)
        log.info("wx notify result:${result}".toString())
    }

    def sendMsg2Teacher(Long campusId, MessageRecord messageRecord, Message message) {
        log.info("【HiiAdmin sendMsg2Teacher】campusId:${campusId} messageRecordId:${messageRecord.id}".toString())
        Teacher teacher = null
        if (message.senderType == ConstantEnum.UserTypeEnum.TEACHER.type) {
            teacher = teacherService.fetchTeacherById(message.senderId)
        }
        Campus campus = campusService.fetchCampusByCampusId(campusId)
        WechatProfile wechatProfile = userService.fetchWechatProfileByOpenId(messageRecord.receiverOpenId)
        User user = userService.fetchUserByUIdAndType(messageRecord.receiverId, ConstantEnum.UserTypeEnum.TEACHER.type)
        MessageApiInvoker.Message2teacher message2teacher = new MessageApiInvoker.Message2teacher(
                openid: messageRecord.receiverOpenId,
                campusName: campus?.name,
                teacherName: teacher?.name,
                messageId: message?.id,
                title: message?.title,
                date: message.lastNotifyTime.time,
                appId: wechatProfile?.appId,
                messageType: 5 as Byte
        )
        if (!user) {
            return "no user error"
        }
        if (messageRecord.receiverOpenId) {
            messageApiInvoker.sendMessage2teacher(message2teacher)
        } else {
            log.warn('【HiiAdmin】接收者没有openid，不发送微信消息,messageId:{},messageRecordId:{}', message.id, messageRecord.id)
        }
        //钉钉校内通知
        dingNoticeService.sendDingMessage2teacher(message.id, message?.title, messageRecord.receiverId, teacher?.name, campus?.agentId, campus?.corpId, campus?.id)
    }

    def fetchAllMessage4senderCate(Long senderId, Byte senderType, Byte cate, String searchValue, Byte type, Long startDate, Long endDate, int p, int s) {
        def c = Message.createCriteria()
        def map = c.list(max: s, offset: (p - 1) * s) {
            eq("senderId", senderId)
            eq("senderType", senderType)
            eq("cate", cate)
            if (type) {
                eq("status", type)
            }
            if (startDate && endDate) {
                Date startTime = TimeUtils.getDateStartTime(startDate)
                Date endTime = TimeUtils.getDateEndTime(endDate)
                between("dateCreated", startTime, endTime)
            }
        }
        [list: map as List<Message>, total: map.totalCount]
    }
    

    Map<String, Object> buildHQL(StringBuilder stringBuilder, Long receiverId, Byte receiverType, Byte cate, String searchValue, Long startDate, Long endDate, int p, int s) {
        Map<String, Object> map = [:]
        String HQL = """ FROM MessageRecord mr, Message m
                                WHERE mr.messageId = m.id
                                    AND m.status = 1
                                    AND mr.receiverId = :receiverId 
                                    AND mr.receiverType = :receiverType 
                                    AND mr.cate = :cate """
        stringBuilder.append(HQL)
        map.put("receiverId", receiverId)
        map.put("receiverType", receiverType)
        map.put("cate", cate)

        if (searchValue) {
            stringBuilder.append(" AND m.publisherName LIKE :searchValue")
            map.put("searchValue", searchValue + '%')
        }

        if (startDate && endDate) {
            Date startTime = TimeUtils.getDateStartTime(startDate)
            Date endTime = TimeUtils.getDateEndTime(endDate)
            stringBuilder.append(" AND mr.dateCreated BETWEEN :startTime AND :endTime ")
            map.put("startTime", startTime)
            map.put("endTime", endTime)
        }
        if (s > 0) {
            map.put("max", s)
            map.put("offset", (p - 1) * s)
        }
        stringBuilder.append(" ORDER BY mr.id DESC ")
        map
    }

    List<MessageRecord> fetchAllMessageRecord4receiverCate(Long receiverId, Byte receiverType, Byte cate, String searchValue, Long startDate, Long endDate, int p, int s) {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("SELECT mr ")
        Map<String, Object> map = buildHQL(stringBuilder, receiverId, receiverType, cate, searchValue, startDate, endDate, p, s)
        MessageRecord.executeQuery(stringBuilder, map)
    }

    Integer countMessageRecord4receiverCate(Long receiverId, Byte receiverType, Byte cate, String searchValue, Long startDate, Long endDate) {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("SELECT COUNT(1) ")
        Map<String, Object> map = buildHQL(stringBuilder, receiverId, receiverType, cate, searchValue, startDate, endDate, 0, 0)
        MessageRecord.executeQuery(stringBuilder, map)[0] as Integer
    }

    @Cached(expire = 1, timeUnit = TimeUnit.DAYS, name = "hii_messageS_fetchMessageById", key = "#msgId",
            cacheNullValue = false, cacheType = CacheType.REMOTE, condition = "#cache eq null or #cache")
    @CacheRefresh(refresh = 1, stopRefreshAfterLastAccess = 2, timeUnit = TimeUnit.SECONDS)
    Message fetchMessageById(Long msgId, boolean cache = true) {
        Message.get(msgId)
    }

    MessageRecord fetchMessageRecordByMessageIdAndReceiverId(Long messageId, Long receiverId, Byte receiverType) {
        MessageRecord.findByMessageIdAndReceiverIdAndReceiverType(messageId, receiverId, receiverType)
    }

    Message fetchMessageByIdAndStatus(Long messageId, Byte status) {
        Message.findByIdAndStatus(messageId, status)
    }

    MessageRecord saveMessageRecord(MessageRecord messageRecord) {
        messageRecord.save(failOnError: true, flush: true)
    }

    Message saveMessage(Message message) {
        message.save(failOnError: true, flush: true)
    }

    /**
     * 撤回 Message，撤回MessageRecord
     * @param msgId
     */
    void withdrawMessage(Long msgId) {
        String messageHQL = "UPDATE Message SET status = :status WHERE id = :msgId"
        Message.executeUpdate(messageHQL, [msgId: msgId, status: ConstantEnum.MessageStatus.DELETE.status])
        String messageRecordHQL = "UPDATE MessageRecord SET status = :status WHERE messageId = :msgId"
        MessageRecord.executeUpdate(messageRecordHQL, [msgId: msgId, status: ConstantEnum.MessageStatus.DELETE.status])
    }

    /**
     * update message status
     */
    def updateMessageStatus(Long msgId, String title, String content, String url, String teacherIds, String pic, String enclosure, Byte requireConfirm, String json, int total){
        Message message = fetchMessageById(msgId)
        if (message){
            message.title = title
            message.content = content
            message.url = url
            message.ids = teacherIds
            message.pic = pic
            message.enclosure = enclosure
            message.requireConfirm = requireConfirm
            message.json = json
            message.total = total
            message.lastUpdated = new Date()
            message.lastNotifyTime = new Date()
            message.status = ConstantEnum.MessageStatus.SENT.status
            message.save(failOnError: true, flush: true)
        }
    }
}

```

