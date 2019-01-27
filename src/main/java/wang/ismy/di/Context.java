package wang.ismy.di;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {


    public static void init() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Map<Class, Node<? extends Object>> container = new ConcurrentHashMap<>();

    public static void bind(Class klass) throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        if (get(klass) != null) return;
        Node node = new Node();
        ClassHolder holder = new ClassHolder();
        node.setElement(initObject(klass, holder));
        container.put(holder.getKlass(), node);

    }


    public static Object get(Class klass) {
        // 遍历一下，容器当中是否有klass的实现类或者子类
        try {
            for (var i : container.keySet()) {
                if (i.getSuperclass().equals(klass)) {
                    klass = i;
                    break;
                }

                for (var j : i.getInterfaces()) {
                    if (j.equals(klass)) {
                        klass = i;
                        throw new Exception();
                    }
                }
            }
        } catch (Exception e) {

        }
        if (container.get(klass) == null) return null;
        return container.get(klass).getElement();
    }

    /*
     * 初始化对象
     */
    public static Object initObject(Class klass, ClassHolder holder) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Constructor constructor = mathConstructor(klass.getConstructors());
        Object[] params = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            Object obj = null;
            // 如果在容器当中有该类型的对象，那就直接从容器中获取
            Class<?> type = constructor.getParameters()[i].getType();
            if (get(type) != null) {
                obj = get(type);
            } else {
                // 如果该参数是接口类型，那先载入其所有实现类
                if (type.isInterface()){
                    bindAllImplement(type);
                    obj = get(type);
                }else{
                    obj = initObject(type, new ClassHolder());
                }

            }
            params[i] = obj;

        }
        holder.setKlass(klass);
        if (get(klass) != null) return get(klass);

        Object ret = constructor.newInstance(params);
        Node node = new Node();
        node.setElement(ret);
        container.put(klass,node);
        return ret;
    }

    private static void bindAllImplement(Class<?> type) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {

        if (!type.isInterface()) return;

        var list = getAllClasses();

        for (var i : list){
            Class currentClass = Class.forName(i);
            for (var j : currentClass.getInterfaces()){
                if (j == type){
                    for (var k : currentClass.getAnnotations()){
                        if (k.annotationType() == Component.class){
                            bind(currentClass);
                        }
                    }

                }
            }
        }
    }

    public static Constructor mathConstructor(Constructor[] constructors) {
        Constructor constructor = null;
        for (var i : constructors) {
            if (i.getParameterCount() == 0) {
                constructor = i;
            } else if (constructor != null && i.getParameterCount() < constructor.getParameterCount()) {
                constructor = i;
            } else if (constructor == null) {
                constructor = i;
            }
        }
        return constructor;
    }

    public static void scanAllClasses() {
        String url = URLDecoder.decode(Context.class.getResource("/").getPath(), Charset.defaultCharset());

        if (url.startsWith("/")) {
            url = url.replaceFirst("/", "");
        }

        url = url.replaceAll("/", "\\\\");


        File file = new File(url);

        List<String> classes = getAllClass(file);

        for (int i = 0; i < classes.size(); i++) {
            classes.set(i, classes.get(i).replace(url, "").replace(".class", "").replace("\\", "."));
        }


        // 遍历classes，发现@Component就注入到容器中

        for (var i : classes) {
            try {
                var anno = Class.forName(i).getAnnotations();

                for (var j : anno) {

                    if (j.annotationType() == Component.class) {

                        bind(Class.forName(i));
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public static List<String> getAllClasses() {
        String url = URLDecoder.decode(Context.class.getResource("/").getPath(), Charset.defaultCharset());

        if (url.startsWith("/")) {
            url = url.replaceFirst("/", "");
        }

        url = url.replaceAll("/", "\\\\");


        File file = new File(url);

        List<String> classes = getAllClass(file);

        for (int i = 0; i < classes.size(); i++) {
            classes.set(i, classes.get(i).replace(url, "").replace(".class", "").replace("\\", "."));
        }

        return classes;

    }

    private static List<String> getAllClass(File file) {
        List<String> ret = new ArrayList<>();
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (var i : list) {
                var j = getAllClass(i);
                ret.addAll(j);
            }
        } else {

            ret.add(file.getAbsolutePath());
        }
        return ret;
    }
}
