package wang.ismy.di;


import lombok.Data;

@Data
public class Node<T> {

    private T element;

    private T next;


}
