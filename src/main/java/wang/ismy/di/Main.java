package wang.ismy.di;


import net.sf.cglib.proxy.Enhancer;

public class Main {

    public static void main(String[] args) {
        Context.scanAllClasses();
        Context.aop(aspect -> {

            System.out.println(aspect.getMethod()+"被运行");
            return aspect.process();
        });
        Service service = (Service) Context.get(Service.class);
        service.get();

    }
}
