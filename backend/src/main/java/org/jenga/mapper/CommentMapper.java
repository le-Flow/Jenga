package org.jenga.mapper;

import org.jenga.model.Comment;
import org.jenga.dto.CommentRequestDTO;
import org.jenga.dto.CommentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    Comment commentRequestDTOToComment(CommentRequestDTO commentDTO);

    @Mapping(source = "author.username", target = "author")
    CommentResponseDTO commentToCommentResponseDTO(Comment comment);
}
