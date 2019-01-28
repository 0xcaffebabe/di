package wang.ismy.di;


@Component
public class DAO {
    {
        System.out.println("DAO被新建");
    }

    public void update(){
        System.out.println("更新");
    }
}
