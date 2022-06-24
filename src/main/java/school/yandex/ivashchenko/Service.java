package school.yandex.ivashchenko;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.yandex.ivashchenko.DTO.DTOEntityConverter;
import school.yandex.ivashchenko.DTO.Item;
import school.yandex.ivashchenko.DTO.ItemsArray;
import school.yandex.ivashchenko.DTO.NodeInfo;
import school.yandex.ivashchenko.entity.Category;
import school.yandex.ivashchenko.entity.Offer;
import school.yandex.ivashchenko.repository.CategoryRepository;
import school.yandex.ivashchenko.repository.OfferRepository;

import java.util.ArrayList;

@Component
public class Service {
    private final OfferRepository offerRepository;
    private final CategoryRepository categoryRepository;
    private final DTOEntityConverter dtoEntityConverter;

    @Autowired
    public Service(final OfferRepository offerRepository, final CategoryRepository categoryRepository,
                   final DTOEntityConverter dtoEntityConverter) {
        this.offerRepository = offerRepository;
        this.categoryRepository = categoryRepository;
        this.dtoEntityConverter = dtoEntityConverter;
    }

    public ResponseEntity processImport(ItemsArray itemsArray){
        if (itemsArray != null) {
            for (Item i : itemsArray.items) {
                i.updateDate = itemsArray.updateDate;
                if (i.type.equals("CATEGORY")) {
                    Category category = dtoEntityConverter.itemToCategory(i);
                    categoryRepository.save(category);
                } else if (i.type.equals("OFFER")){
                    Category category = categoryRepository.findEntityById(i.parentId);
                    Offer offer = dtoEntityConverter.itemToOffer(i, category);
                    offerRepository.save(offer);
                }
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity processDelete(String id) {
        if (offerRepository.findById(id).isPresent()) {
            offerRepository.deleteById(id);
        } else if (categoryRepository.findById(id).isPresent()) {
            categoryRepository.deleteById(id);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public NodeInfo processInfo(String id){
        NodeInfo nodeInfo = null;
        if (offerRepository.findById(id).isPresent()){
            Offer offer = offerRepository.findEntityById(id);
            nodeInfo = dtoEntityConverter.OfferToNodeInfo(offer);
        } else if (categoryRepository.findById(id).isPresent()) {
            Category category = categoryRepository.findEntityById(id);
            nodeInfo = new NodeInfo();
            nodeInfo.children = getAllChildren(category);
            dtoEntityConverter.CategoryToNodeInfo(category, nodeInfo);
        } else {
            categoryRepository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Not Found"));
        }
        return nodeInfo;
    }

    int offerCount;
    int offerPriceSum;
    public ArrayList getAllChildren(Category category){
        ArrayList<NodeInfo> nodeInfoList = new ArrayList<>();
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
                NodeInfo nodeInfo = dtoEntityConverter.OfferToNodeInfo(offer);
                nodeInfoList.add(nodeInfo);
            }
            categoryRepository.updateCategoryPrice(localPriceSum / localCount, category.getId());
            category.setPrice(localPriceSum / localCount);
        } else if (!categoryList.isEmpty()) {
            for (Category childCategory : categoryList) {
                NodeInfo nodeInfo = new NodeInfo();
                nodeInfo.children = getAllChildren(childCategory);
                int categoryPrice = categoryRepository.selectPrice(childCategory.getId());
                childCategory.setPrice(categoryPrice);
                dtoEntityConverter.CategoryToNodeInfo(childCategory, nodeInfo);
                nodeInfoList.add(nodeInfo);
            }
            if (offerCount != 0) {
                categoryRepository.updateCategoryPrice(offerPriceSum / offerCount, category.getId());
                category.setPrice(offerPriceSum / offerCount);
            }
        }
        return nodeInfoList;
    }
}