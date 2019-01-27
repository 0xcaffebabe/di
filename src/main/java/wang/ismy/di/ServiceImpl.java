package wang.ismy.di;

@Component
public class ServiceImpl implements Service {

    private DAO dao;
    {
        System.out.println("service 被创建");
    }

    public ServiceImpl(DAO dao){
        this.dao = dao;
    }
}
