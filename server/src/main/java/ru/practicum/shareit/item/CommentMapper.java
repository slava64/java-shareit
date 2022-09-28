package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> commentDto(Collection<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment: comments
             ) {
            commentsDto.add(toCommentDto(comment));
        }
        return commentsDto;
    }

    public static Comment toComment(CommentPostDto commentDto, User autor, Item item) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(autor);
        comment.setItem(item);
        return comment;
    }
}
