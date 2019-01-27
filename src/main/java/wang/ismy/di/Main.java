package wang.ismy.di;

import java.lang.reflect.InvocationTargetException;


public class Main {

    public static void main(String[] args) {
        Context.scanAllClasses();
        System.out.println(Context.get(Controller.class));
        System.out.println(Context.get(Service.class));

    }
}
