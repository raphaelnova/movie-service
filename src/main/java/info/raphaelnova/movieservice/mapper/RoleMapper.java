package info.raphaelnova.movieservice.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import info.raphaelnova.movieservice.entity.RolesEntity;
import info.raphaelnova.movieservice.generated.proto.Roles;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    uses = { PersonMapper.class })
public interface RoleMapper {

    Roles toRoles(RolesEntity rolesEntity);

    RolesEntity toRolesEntity(Roles roles);
}
