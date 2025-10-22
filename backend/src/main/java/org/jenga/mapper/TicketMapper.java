package org.jenga.mapper;

import org.jenga.model.Ticket;
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
    TicketDTO ticketToTicketDTO(Ticket ticket);

    @Mapping(source = "projectName", target = "project.name")
    @Mapping(source = "assigneeName", target = "assignee.username")
    Ticket ticketDTOToTicket(TicketDTO ticketDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Ticket createTicketDTOToTicket(CreateTicketDTO createTicketDTO);
}
