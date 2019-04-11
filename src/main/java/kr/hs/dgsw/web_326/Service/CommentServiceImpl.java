package kr.hs.dgsw.web_326.Service;

import kr.hs.dgsw.web_326.Domain.Comment;
import kr.hs.dgsw.web_326.Domain.User;
import kr.hs.dgsw.web_326.Protocol.CommentUsernameProtocol;
import kr.hs.dgsw.web_326.Repository.CommentRepository;
import kr.hs.dgsw.web_326.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @PostConstruct
    private void init() {
        User u = userRepository.save(new User("aaa", "aaa@dgsw", "1234",
                "E:/Source/IdeaProjects/web_326/upload/2019/04/10/c21d69b2-2074-4f32-acab-958320a2bac1_person.png", "person.png"));
        commentRepository.save(new Comment(u.getId(), "hi there 1",
                "E:/Source/IdeaProjects/web_326/upload/2019/04/10/f68402fe-2527-4a38-afbc-b297b18c2502_content.png", "content.png"));
        commentRepository.save(new Comment(u.getId(), "hi there 2",
                "E:/Source/IdeaProjects/web_326/upload/2019/04/10/f68402fe-2527-4a38-afbc-b297b18c2502_content.png", "content.png"));
        commentRepository.save(new Comment(u.getId(), "hi there 3",
                "E:/Source/IdeaProjects/web_326/upload/2019/04/10/f68402fe-2527-4a38-afbc-b297b18c2502_content.png", "content.png"));
    }

    @Override
    public CommentUsernameProtocol add(Comment comment) {
//        Optional<User> user = userRepository.findById(comment.getUserId());
//        if (user.isPresent()) added = commentRepository.save(comment);
        return new CommentUsernameProtocol(
                commentRepository.save(comment),
                userRepository.findById(comment.getUserId())
                        .map(found -> found.getUsername())
                        .orElse(null));
    }

    @Override
    public boolean remove(Long id) {
        try {
            commentRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CommentUsernameProtocol update(Long id, Comment comment) {
        Optional<User> user = userRepository.findById(comment.getUserId());
        if (user.isPresent()) {
            return commentRepository.findById(id)
                    .map(found -> {
                        found.setContent(Optional.ofNullable(comment.getContent()).orElse(found.getContent()));
                        found.setStoredPath(Optional.ofNullable(comment.getStoredPath()).orElse(found.getStoredPath()));
                        found.setOriginalName(Optional.ofNullable(comment.getOriginalName()).orElse(found.getOriginalName()));
                        commentRepository.save(found);
                        return new CommentUsernameProtocol(found, user.get().getUsername());
                    })
                    .orElse(null);
        }
        return null;
    }

    @Override
    public CommentUsernameProtocol view(Long id) {
        return commentRepository.findById(id)
                .map(found -> {
                    Optional<User> user = userRepository.findById(found.getUserId());
                    String username = user.isPresent() ? user.get().getUsername() : null;
                    return new CommentUsernameProtocol(found, username);
                })
                .orElse(null);
    }

    @Override
    public List<CommentUsernameProtocol> listAllComments() {
        List<Comment> commentList = commentRepository.findAll();
        List<CommentUsernameProtocol> cupList = new ArrayList<>();
        commentList.forEach(comment -> {
            Optional<User> found = userRepository.findById(comment.getUserId());
            String username = found.isPresent() ? found.get().getUsername() : null;
            cupList.add(new CommentUsernameProtocol(comment, username));
        });
        return cupList;
    }
}
