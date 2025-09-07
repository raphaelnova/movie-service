package info.raphaelnova.movieservice.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Node("Movie")
public class MovieEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String tagline;

    private Integer released;

    @Relationship(type = "ACTED_IN", direction = Direction.INCOMING)
    private List<RolesEntity> actorsAndRoles = new ArrayList<>();

    @Relationship(type = "DIRECTED", direction = Direction.INCOMING)
    private List<PersonEntity> directors = new ArrayList<>();
}