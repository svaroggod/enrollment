package school.yandex.ivashchenko.service;

import org.springframework.stereotype.Component;
import school.yandex.ivashchenko.DTO.Item;
import school.yandex.ivashchenko.DTO.Items_Array;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Validate {
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public boolean isWrong(Items_Array itemsArray) {
        try {
            Date date = inputFormat.parse(itemsArray.updateDate);
        } catch (ParseException e) {
            return true;
        }
        String id = null;

        for (Item item : itemsArray.items) {
            if (item.name == null || item.id.equals(id)) {
                return true;
            } else if (item.type.equals("OFFER")) {
                if (item.price == null && item.price < 0) {
                    return true;
                }
            } else if (item.type.equals("CATEGORY")) {
                if (item.price != null) {
                    return true;
                }
            }
            id = item.id;
        }
        return false;
    }
}