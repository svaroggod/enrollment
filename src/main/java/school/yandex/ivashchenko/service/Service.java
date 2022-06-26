package school.yandex.ivashchenko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.yandex.ivashchenko.DTO.DTO_To_Entity_Convert;
import school.yandex.ivashchenko.DTO.Item;
import school.yandex.ivashchenko.DTO.Items_Array;
import school.yandex.ivashchenko.DTO.Node_Info;
import school.yandex.ivashchenko.entity.Category;
import school.yandex.ivashchenko.entity.Offer;
import school.yandex.ivashchenko.repository.Category_Repository;
import school.yandex.ivashchenko.repository.Offer_Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class Service {
    private final Offer_Repository offerRepository;
    private final Category_Repository categoryRepository;
    private final DTO_To_Entity_Convert dtoEntityConverter;
    private final Validate validator;
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    int offerCount;
    int offerPriceSum;

    @Autowired
    public Service(final Offer_Repository offerRepository, final Category_Repository categoryRepository,
                   final DTO_To_Entity_Convert dtoEntityConverter, final Validate validator) {
        this.offerRepository = offerRepository;
        this.categoryRepository = categoryRepository;
        this.dtoEntityConverter = dtoEntityConverter;
        this.validator = validator;
    }

    public ResponseEntity process_Import(Items_Array itemsArray) {
        if (itemsArray != null) {
            if (validator.isWrong(itemsArray)) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            for (Item i : itemsArray.items) {
                i.updateDate = itemsArray.updateDate;
                if (i.type.equals("CATEGORY")) {
                    Category category = dtoEntityConverter.item_To_Category(i);
                    categoryRepository.save(category);
                } else if (i.type.equals("OFFER")) {
                    Category category = categoryRepository.findEntityById(i.parentId);
                    Offer offer = dtoEntityConverter.item_To_Offer(i, category);
                    offerRepository.save(offer);
                }
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity process_Delete(String id) {
        if (offerRepository.findById(id).isPresent()) {
            offerRepository.deleteById(id);
        } else if (categoryRepository.findById(id).isPresent()) {
            delete_All_SubCategories(id);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public void delete_All_SubCategories(String id) {
        ArrayList<String> subIdList = categoryRepository.selectIdByParentId(id);
        if (!subIdList.isEmpty()) {
            for (String subId : subIdList) {
                delete_All_SubCategories(subId);
            }
        }
        categoryRepository.deleteById(id);
    }

    public Node_Info process_Info(String id) {
        Node_Info nodeInfo = null;
        if (offerRepository.findById(id).isPresent()) {
            Offer offer = offerRepository.findEntityById(id);
            nodeInfo = dtoEntityConverter.Offer_To_Node_Info(offer);
        } else if (categoryRepository.findById(id).isPresent()) {
            Category category = categoryRepository.findEntityById(id);
            nodeInfo = new Node_Info();
            nodeInfo.children = get_All_Children(category);
            dtoEntityConverter.Category_To_Node_Info(category, nodeInfo);
            set_Date(nodeInfo);
        } else {
            categoryRepository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Not Found"));
        }
        return nodeInfo;
    }

    public ArrayList get_All_Children(Category category) {
        ArrayList<Node_Info> nodeInfoList = new ArrayList<>();
        String id = category.getId();

        ArrayList<Offer> offerList = offerRepository.findAllByCategory(category);
        ArrayList<Category> categoryList = categoryRepository.findAllByParentId(id);

        if (!offerList.isEmpty()) {
            int localCount = 0;
            int localPriceSum = 0;
            for (Offer offer : offerList) {
                localPriceSum += offer.getPrice();
                offerPriceSum += offer.getPrice();
                localCount++;
                offerCount++;
                Node_Info nodeInfo = dtoEntityConverter.Offer_To_Node_Info(offer);
                nodeInfoList.add(nodeInfo);
            }
            categoryRepository.updateCategoryPrice(localPriceSum / localCount, category.getId());
            category.setPrice(localPriceSum / localCount);
        } else if (!categoryList.isEmpty()) {
            for (Category childCategory : categoryList) {
                Node_Info nodeInfo = new Node_Info();
                nodeInfo.children = get_All_Children(childCategory);
                int categoryPrice = categoryRepository.selectPrice(childCategory.getId());
                childCategory.setPrice(categoryPrice);
                dtoEntityConverter.Category_To_Node_Info(childCategory, nodeInfo);
                nodeInfoList.add(nodeInfo);
            }
            if (offerCount != 0) {
                categoryRepository.updateCategoryPrice(offerPriceSum / offerCount, category.getId());
                category.setPrice(offerPriceSum / offerCount);
            }
            return nodeInfoList;
        }
        return nodeInfoList;
    }

    public void set_Date(Node_Info nodeInfo) {
        try {
            Date dateParent = inputFormat.parse(nodeInfo.date);
            if (nodeInfo.children != null && !nodeInfo.children.isEmpty()) {
                for (Node_Info child : nodeInfo.children) {
                    Date dateChild = inputFormat.parse(child.date);
                    set_Date(child);
                    if (dateParent.before(dateChild)) {
                        nodeInfo.date = child.date;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}