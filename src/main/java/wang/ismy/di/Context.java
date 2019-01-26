package wang.ismy.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static Map<Class,Node<? extends Object>> container = new ConcurrentHashMap<>();

    public static void bind(Class klass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Node node = new Node();
        ClassHolder holder = new ClassHolder();
        node.setElement(initObject(klass,holder));
        container.put(holder.getKlass(),node);

    }



    public static Object get(Class klass){
        // 遍历一下，容器当中是否有klass的实现类或者子类
        try {
            for (var i : container.keySet()){
                if (i.getSuperclass().equals(klass)){
                    klass = i;
                    break;
                }

                for (var j : i.getInterfaces()){
                    if (j.equals(klass)){
                        klass = i;
                        throw new Exception();
                    }
                }
            }
        }catch (Exception e){

        }
        if (container.get(klass) == null) return null;
        return container.get(klass).getElement();
    }
    /*
    * 初始化对象
    */
    public static Object initObject(Class klass,ClassHolder holder) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        // 遍历一下，容器当中是否有klass的实现类或者子类
//        try {
//            for (var i : container.keySet()){
//                if (i.getSuperclass().equals(klass)){
//                    klass = i;
//                    break;
//                }
//
//                for (var j : i.getInterfaces()){
//                    if (j.equals(klass)){
//                        klass = i;
//                        throw new Exception();
//                    }
//                }
//            }
//        }catch (Exception e){
//
//        }


        Constructor constructor = mathConstructor(klass.getConstructors());
        Object[] params = new Object[constructor.getParameterCount()];
        for (int i = 0;i<constructor.getParameterCount();i++){
            Object obj = null;
            // 如果在容器当中有该类型的对象，那就直接从容器中获取
            if (get(constructor.getParameters()[i].getType()) != null){
                obj = get(constructor.getParameters()[i].getType());
            }else{
                obj = initObject(constructor.getParameters()[i].getType(),new ClassHolder());
            }
            params[i]=obj;

        }
        holder.setKlass(klass);
//        if (params.length >0)
//        System.out.println(params[0]);
        return constructor.newInstance(params);
    }

    public static Constructor mathConstructor(Constructor[] constructors){
        Constructor constructor = null;
        for (var i : constructors){
            if (i.getParameterCount() == 0){
                constructor = i;
            }else if (constructor != null && i.getParameterCount() < constructor.getParameterCount()){
                constructor = i;
            }else if (constructor == null){
                constructor = i;
            }
        }
        return constructor;
    }
}
