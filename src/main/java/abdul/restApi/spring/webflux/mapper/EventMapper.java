package abdul.restApi.spring.webflux.mapper;

import abdul.restApi.spring.webflux.dto.EventDto;
import abdul.restApi.spring.webflux.model.Event;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto map(Event event);

    @InheritInverseConfiguration
    Event map(EventDto eventDto);
}
