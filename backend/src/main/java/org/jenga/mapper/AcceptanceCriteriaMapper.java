package org.jenga.mapper;

import org.jenga.dto.AcceptanceCriteriaRequest;
import org.jenga.dto.AcceptanceCriteriaResponse;
import org.jenga.model.AcceptanceCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface AcceptanceCriteriaMapper {

    AcceptanceCriteriaMapper INSTANCE = Mappers.getMapper(AcceptanceCriteriaMapper.class);

    AcceptanceCriteria toEntity(AcceptanceCriteriaRequest request);

    AcceptanceCriteriaResponse toResponse(AcceptanceCriteria entity);
}
