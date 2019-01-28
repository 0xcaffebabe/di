package wang.ismy.di;

import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Data
public class Aspect {
    private Object o;
    private Method method;
    private Object[] objects;
    private MethodProxy methodProxy;

    public Aspect(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        this.o = o;
        this.method = method;
        this.objects = objects;
        this.methodProxy = methodProxy;
    }

    public Object process(Object...params) throws Throwable {
        return methodProxy.invokeSuper(o, params);
    }

    public Object process() throws Throwable {
        return methodProxy.invokeSuper(o, objects);
    }
}
