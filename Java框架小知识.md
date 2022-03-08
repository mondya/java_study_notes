# @requestparam和@PathVariable和@param和@RequestParam区别
## @Requestparam
> http://localhost:8080/springmvc/hello/101?param1=10&param2=20
```java
public String getDetails(
    @RequestParam(value="param1", required=true) String param1,
        @RequestParam(value="param2", required=false) String param2){
...
}
```
@Requestparam支持四种参数
- defaultValue默认值
- required表示是否为必须，默认是true，表示不能为空
- value参数名字
- name参数名字，和value属性作用相同
## @PathVariable
> http://localhost:8080/springmvc/hello/101?param1=10&param2=20
```java
@RequestMapping("/hello/{id}")
    public String getDetails(@PathVariable(value="id") String id,
    @RequestParam(value="param1", required=true) String param1,
    @RequestParam(value="param2", required=false) String param2){
.......
}
```
## @Param
mybatis的注解，用于dao/mapper层，与mapper.xml字段对应
## @RequestBody
在post请求中，当访问URL为`/user/UpdateAddressByUaId`
```java
@RequestMapping(value = "/user/UpdateAddressByUaId")
    public R UpdateAddressByUaId(@RequestBody UserAddress userAddress){
        R rrr = myService.confirmAddress(userAddress);
        return rrr;
    }
```
