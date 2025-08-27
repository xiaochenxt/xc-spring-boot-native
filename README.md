直接引入依赖
- Maven:
```xml
<dependency>
    <groupId>io.github.xiaochenxt</groupId>
    <artifactId>xc-spring-boot-native</artifactId>
    <version>0.0.4</version>
</dependency>
```
springboot项目需先执行spring-boot:process-aot
最后执行native:compile-no-fork既可编译成功
