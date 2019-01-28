# di

#
2019-1-26

根据个人对依赖注入原理理解的一个简单实现

#
测试用例:

DAO:
```java
@Component
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
@Component
public class ServiceImpl implements Service {

    private DAO dao;
    {
        System.out.println("service 被创建");
    }

    public ServiceImpl(DAO dao){
        this.dao = dao;
    }

    @Override
    public void get() {
        System.out.println("get");
    }
}
```
Controller:
```java
@Component
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
Main:
```java
public class Main {

    public static void main(String[] args) {
        Context.scanAllClasses();
        System.out.println(Context.get(Controller.class));
        System.out.println(Context.get(Service.class));
        
    }
}
```
最终输出:
```text
DAO被新建
service 被创建
controller 被新建
wang.ismy.di.Controller@668bc3d5
wang.ismy.di.ServiceImpl@3cda1055
```
#
AOP支持:
```java
public class Main {

    public static void main(String[] args) {
        Context.scanAllClasses();
        Context.aop(aspect -> {
            System.out.println(aspect.getMethod()+"被运行");
            return aspect.process();
        });
        Service service = Context.get(Service.class);
        service.get();

    }
}
```
输出:
```text
DAO被新建
service 被创建
controller 被新建
public void wang.ismy.di.ServiceImpl.get()被运行
get
```

