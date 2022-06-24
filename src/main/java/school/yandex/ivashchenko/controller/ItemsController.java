package school.yandex.ivashchenko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.yandex.ivashchenko.DTO.ItemsArray;
import school.yandex.ivashchenko.DTO.NodeInfo;
import school.yandex.ivashchenko.Service;

@RestController
@RequestMapping
public class ItemsController {
    private final Service service;

    @Autowired
    public ItemsController(final Service service) {
        this.service = service;
    }

    @PostMapping(value = "/imports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity importItems(@RequestBody ItemsArray itemsArray) {
        return service.processImport(itemsArray);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        return service.processDelete(id);
    }

    @GetMapping("/nodes/{id}")
    public NodeInfo infoById(@PathVariable("id") String id) {
        return service.processInfo(id);
    }
}