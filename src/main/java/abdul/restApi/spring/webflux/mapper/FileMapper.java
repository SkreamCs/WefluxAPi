package abdul.restApi.spring.webflux.mapper;

import abdul.restApi.spring.webflux.dto.FileDto;
import abdul.restApi.spring.webflux.model.File;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FileMapper {
    FileDto map(File file);

    @InheritInverseConfiguration
    File map(FileDto fileDto);

}
