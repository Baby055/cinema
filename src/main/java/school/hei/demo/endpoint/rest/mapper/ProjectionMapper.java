package school.hei.demo.endpoint.rest.mapper;

import org.springframework.stereotype.Component;
import school.hei.demo.endpoint.rest.model.ProjectionRest;
import school.hei.demo.model.Projection;

@Component
public class ProjectionMapper {
    public ProjectionRest toRest(Projection projection){
        return new ProjectionRest(
                projection.getId(),
                projection.getDatetime(),
                projection.getSeatPrice(),
                projection.getMovie().getUuid(),
                projection.getRoom().getId()
        );
    }
}
