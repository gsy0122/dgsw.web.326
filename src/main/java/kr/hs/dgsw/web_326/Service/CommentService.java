package kr.hs.dgsw.web_326.Service;

import kr.hs.dgsw.web_326.Domain.Comment;
import kr.hs.dgsw.web_326.Protocol.CommentUsernameProtocol;

import java.util.List;

public interface CommentService {
    CommentUsernameProtocol add(Comment comment);
    boolean remove(Long id);
    CommentUsernameProtocol update(Long id, Comment comment);
    CommentUsernameProtocol view(Long id);
    List<CommentUsernameProtocol> listAllComments();
}
