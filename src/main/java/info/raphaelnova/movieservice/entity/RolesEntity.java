package info.raphaelnova.movieservice.entity;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RelationshipProperties
public class RolesEntity {

    @Id @GeneratedValue
    private Long id;

    @TargetNode
    private PersonEntity actor;

    private List<String> roles;
}
