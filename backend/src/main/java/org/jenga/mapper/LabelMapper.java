package org.jenga.mapper;

import org.jenga.model.Label;
import org.jenga.dto.LabelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface LabelMapper {

    LabelMapper INSTANCE = Mappers.getMapper(LabelMapper.class);

    LabelDTO labelToLabelDTO(Label label);

    Label labelDTOToLabel(LabelDTO labelDTO);
}
