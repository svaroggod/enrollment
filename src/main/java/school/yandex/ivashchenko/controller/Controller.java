package school.yandex.ivashchenko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.yandex.ivashchenko.DTO.Items_Array;
import school.yandex.ivashchenko.DTO.Node_Info;
import school.yandex.ivashchenko.service.Service;

@RestController
@RequestMapping
public class Controller {
    private final Service service;

    @Autowired
    public Controller(final Service service) {
        this.service = service;
    }

    @PostMapping(value = "/imports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity import_Items(@RequestBody Items_Array itemsArray) {
        return service.process_Import(itemsArray);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete_By_Id(@PathVariable("id") String id) {
        return service.process_Delete(id);
    }

    @GetMapping("/nodes/{id}")
    public Node_Info info_By_Id(@PathVariable("id") String id) {
        return service.process_Info(id);
    }
}