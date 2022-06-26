package school.yandex.ivashchenko.DTO;

import java.util.ArrayList;

public class Node_Info {
    public String id;

    public String type;

    public String name;

    public String parentId;

    public int price;

    public String date;

    public ArrayList<Node_Info> children;

    public Node_Info() {
    }
}