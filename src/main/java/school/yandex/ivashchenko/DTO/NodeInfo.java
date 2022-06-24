package school.yandex.ivashchenko.DTO;

import java.util.ArrayList;

public class NodeInfo {
    public String id;

    public String type;

    public String name;

    public String parentId;

    public int price;

    public String updateDate;

    public ArrayList<NodeInfo> children;

    public NodeInfo(){}
}