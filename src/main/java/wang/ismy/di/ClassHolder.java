package wang.ismy.di;

import lombok.Data;

/*
* 用来存放类
*/
@Data
public class ClassHolder {

    private Class klass;


    public ClassHolder(Class klass) {
        this.klass = klass;
    }

    public ClassHolder(){}
}
