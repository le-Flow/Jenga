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

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "reporter.id", target = "reporterId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    TicketDTO ticketToTicketDTO(Ticket ticket);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "assigneeId", target = "assignee.id")
    Ticket ticketDTOToTicket(TicketDTO ticketDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    Ticket createTicketDTOToTicket(CreateTicketDTO createTicketDTO);
}
