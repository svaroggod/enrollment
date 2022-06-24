package school.yandex.ivashchenko.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "id")
    private String id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category",
    fetch = FetchType.EAGER)
    private List<Offer> offerList;

    @Column(name = "type")
    private String type;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "parentId")
    private String parentId;

    @Null
    @Column(name = "price")
    private int price;

    @Column(name = "updateDate")
    private String updateDate;
}