package school.yandex.ivashchenko.DTO;

import org.springframework.stereotype.Component;
import school.yandex.ivashchenko.entity.Category;
import school.yandex.ivashchenko.entity.Offer;


@Component
public class DTO_To_Entity_Convert {
    public Offer item_To_Offer(Item item, Category category) {
        Offer offer = new Offer();
        offer.setId(item.id);
        offer.setType(item.type);
        offer.setName(item.name);
        offer.setCategory(category);
        offer.setPrice(item.price);
        offer.setUpdateDate(item.updateDate);
        return offer;
    }

    public Category item_To_Category(Item item) {
        Category category = new Category();
        category.setId(item.id);
        category.setName(item.name);
        category.setType(item.type);
        category.setParentId(item.parentId);
        category.setPrice(item.price);
        category.setUpdateDate(item.updateDate);
        return category;
    }

    public Node_Info Offer_To_Node_Info(Offer offer) {
        Node_Info nodeInfo = new Node_Info();
        nodeInfo.id = offer.getId();
        nodeInfo.type = offer.getType();
        nodeInfo.name = offer.getName();
        nodeInfo.parentId = offer.getCategory().getId();
        nodeInfo.price = offer.getPrice();
        nodeInfo.date = offer.getUpdateDate();
        return nodeInfo;
    }

    public void Category_To_Node_Info(Category category, Node_Info nodeInfo) {
        nodeInfo.id = category.getId();
        nodeInfo.type = category.getType();
        nodeInfo.name = category.getName();
        nodeInfo.parentId = category.getParentId();
        nodeInfo.price = category.getPrice();
        nodeInfo.date = category.getUpdateDate();

    }
}