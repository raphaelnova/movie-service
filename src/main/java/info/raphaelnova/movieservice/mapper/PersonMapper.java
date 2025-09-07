package info.raphaelnova.movieservice.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import info.raphaelnova.movieservice.entity.PersonEntity;
import info.raphaelnova.movieservice.generated.proto.Person;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PersonMapper {

    Person toPerson(PersonEntity personEntity);

    PersonEntity toPersonEntity(Person person);
}
