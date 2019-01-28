package wang.ismy.di;



public class Main {

    public static void main(String[] args) {
        Context.scanAllClasses();
        System.out.println(Context.get(Controller.class));
        System.out.println(Context.get(Service.class));
    }
}
