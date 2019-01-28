package wang.ismy.di;

import net.sf.cglib.proxy.Enhancer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static Map<Class, Node> container = new ConcurrentHashMap<>();

    private static AOPSupport aopSupport = new AOPSupport();

    public static void bind(Class klass) throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        if (get(klass) != null) return;
        Node node = new Node();
        ClassHolder holder = new ClassHolder();
        node.setElement(initObject(klass, holder));
        injectNode(holder, node);
    }





    public static <T>T get(Class<? extends T> klass) {
        // 遍历一下，容器当中是否有klass的实现类或者子类
        try {
            for (var i : container.keySet()) {
                if (i.getSuperclass().equals(klass)) {
                    klass = (Class<? extends T>) i;
                    break;
                }

                for (var j : i.getInterfaces()) {
                    if (j.equals(klass)) {
                        klass = (Class<? extends T>) i;
                        throw new Exception();
                    }
                }
            }
        } catch (Exception e) {

        }
        if (container.get(klass) == null) return null;
        return (T) container.get(klass).getElement();
    }

    public static void scanAllClasses() {
        String url = getClassPath();
        List<String> classes = getClassesList(url);
        // 遍历classes，如果发现@Component就注入到容器中
        scanComponent2Container(classes);

    }

    public static void aop(AOPRunnable aopRunnable){
        Context.aopSupport.setAopRunnable(aopRunnable);
    }

    private static void injectNode(ClassHolder holder,Node node) {
        container.put(holder.getKlass(), node);
    }

    private static void scanComponent2Container(List<String> classes) {
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

    private static List<String> getClassesList(String url) {
        File file = new File(url);
        List<String> classes = getAllClass(file);
        for (int i = 0; i < classes.size(); i++) {
            classes.set(i, classes.get(i).replace(url, "").replace(".class", "").replace("\\", "."));
        }
        return classes;
    }

    private static String getClassPath() {
        String url = URLDecoder.decode(Context.class.getResource("/").getPath(), Charset.defaultCharset());
        if (url.startsWith("/")) {
            url = url.replaceFirst("/", "");
        }
        url = url.replaceAll("/", "\\\\");
        return url;
    }

    /*
     * 初始化对象
     */
    private static Object initObject(Class klass, ClassHolder holder) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Constructor constructor = mathConstructor(klass.getConstructors());
        Object[] params = getConstructorParams(constructor);
        holder.setKlass(klass);
        /*
        * 如果容器当中有klass这种类型的对象，那就直接返回，
        * 否则通过反射创建新对象，并且把新对象放入容器中备用
        */
        if (get(klass) != null) return get(klass);
        //Object ret = constructor.newInstance(params);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(holder.getKlass());
        enhancer.setCallback(aopSupport);
        Class[] paramsType = new Class[constructor.getParameterCount()];

        for (int i =0;i<paramsType.length;i++){
            paramsType[i]=constructor.getParameters()[i].getType();
        }

        Object ret = enhancer.create(paramsType,params);
        Node node = new Node();
        node.setElement(ret);

        injectNode(new ClassHolder(klass),node);
        return container.get(klass).getElement();
    }

    /*
    * 传入一个构造器，
    * 内部自动构造出该构造器所需要的全部对象
    */
    private static Object[] getConstructorParams(Constructor constructor) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
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
        return params;
    }

    /*
    * 将所有type的实现类注入到容器当中
    */
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

    /*
    * 根据某种策略从构造器数组中选择最适合的构造器
    */
    private static Constructor mathConstructor(Constructor[] constructors) {
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

    /*
    * 获取本项目的所有类
    */
    private static List<String> getAllClasses() {
        String url = getClassPath();
        List<String> classes = getClassesList(url);
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
