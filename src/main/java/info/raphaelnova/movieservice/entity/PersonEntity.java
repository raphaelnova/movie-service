package info.raphaelnova.movieservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Node("Person")
public class PersonEntity {

    @Id @GeneratedValue
    private Long id;

    private Integer born;

    private String name;
}
