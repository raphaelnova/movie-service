package info.raphaelnova.movieservice.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import info.raphaelnova.movieservice.entity.MovieEntity;
import info.raphaelnova.movieservice.generated.proto.MovieReply;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    uses = { PersonMapper.class, RoleMapper.class })
public interface MovieMapper {

    MovieReply toMovieReply(MovieEntity movieEntity);

    MovieEntity toMovieEntity(MovieReply movieReply);
}
