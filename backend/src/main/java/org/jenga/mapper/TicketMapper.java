package org.jenga.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.jenga.model.Ticket;
import org.jenga.model.Label;
import org.jenga.dto.TicketDTO;
import org.jenga.dto.CreateTicketDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface TicketMapper {

    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "reporter.username", target = "reporterName")
    @Mapping(source = "assignee.username", target = "assigneeName")
    @Mapping(source = "labels", target = "labels")
    TicketDTO ticketToTicketDTO(Ticket ticket);

    @Mapping(source = "projectName", target = "project.name")
    @Mapping(source = "assigneeName", target = "assignee.username")
    @Mapping(source = "labels", target = "labels")
    Ticket ticketDTOToTicket(TicketDTO ticketDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(source = "labels", target = "labels")
    Ticket createTicketDTOToTicket(CreateTicketDTO createTicketDTO);

    // Map List<Label> labels to List<String> labels
    default List<String> mapLabelsToLabelNames(List<Label> labels) {
        if (labels == null) {
            return null;
        }
        return labels.stream()
                     .map(Label::getName)
                     .collect(Collectors.toList());
    }

    // Map List<String> labels to List<Label> labels
    default List<Label> mapLabelNamesToLabels(List<String> labelNames) {
        if (labelNames == null) {
            return null;
        }

        return labelNames.stream()
                         .map(name -> {
                             Label label = new Label();
                             label.setName(name);
                             return label;
                         })
                         .collect(Collectors.toList());
    }
}

