package wang.ismy.di;

public class Controller {
    {
        System.out.println("controller 被新建");
    }
    private Service service;

    public Controller(Service service) {
        this.service = service;
    }
}
