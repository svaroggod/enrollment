package school.yandex.ivashchenko.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "offer")
public class Offer {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "type")
    private String type;

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @JoinColumn(name = "parentId")
    private Category category;

    @NotNull
    @Min(value = 0)
    @Column(name = "price")
    private Integer price;

    @Column(name = "updateDate")
    private String updateDate;
}