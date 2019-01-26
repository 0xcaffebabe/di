package wang.ismy.di;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

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