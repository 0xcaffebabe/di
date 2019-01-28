package wang.ismy.di;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class AOPSupport implements MethodInterceptor {


    private AOPRunnable aopRunnable = aspect -> {
        System.out.println(aspect.getMethod()+"运行");
        return aspect.process();
    };

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        Aspect aspect = new Aspect(o,method,objects,methodProxy);
        return aopRunnable.run(aspect);
    }

    public void setAopRunnable(AOPRunnable aopRunnable){
        this.aopRunnable = aopRunnable;
    }




}
