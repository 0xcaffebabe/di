# di

#
2019-1-26

根据个人对依赖注入原理理解的一个简单实现

#
测试用例:

DAO:
```java
public class DAO {

    {
        System.out.println("DAO被新建");
    }
}
```
Service:
```java
public interface Service{
    
}
```
ServiceImpl:
```java
public class ServiceImpl implements Service {

    private DAO dao;
    {
        System.out.println("service 被 创建");
    }

    public ServiceImpl(DAO dao){
        this.dao = dao;
    }
}
```
Controller:
```java
public class Controller {
    {
        System.out.println("controller 被新建");
    }
    private Service service;

    public Controller(Service service) {
        this.service = service;
    }
}
```
Test:
```java
public class ContextTest {

    @Test
    public void bind() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Context.bind(ServiceImpl.class);
        System.out.println("-------");
        Context.bind(Controller.class);

        System.out.println(Context.get(Service.class));
        System.out.println(Context.get(Controller.class));
    }
}
```
最终输出:
```text
DAO被新建
service 被创建
-------
controller 被新建
wang.ismy.di.ServiceImpl@64f6106c
wang.ismy.di.Controller@553a3d88
```
