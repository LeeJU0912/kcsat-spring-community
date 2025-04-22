package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostDetailForm;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostWriteForm;
import hpclab.kcsatspringcommunity.community.repository.PostRepository;
import hpclab.kcsatspringcommunity.myBook.service.BookQuestionService;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final MemberService memberService;
    private final QuestionService questionService;
    private final BookQuestionService bookQuestionService;

    private final RedisTemplate<String, String> redisTemplate;


    // 게시글 저장
    @Override
    public Long savePost(PostWriteForm postWriteForm, String email) {
        Member member = memberService.findMemberByEmail(email);
        Post result;

        if (postWriteForm.getQId() == null) {
            result = Post.builder()
                    .postTitle(postWriteForm.getTitle())
                    .postContent(postWriteForm.getContent())
                    .member(member)
                    .build();
        }
        else {
            Question question = questionService.getQuestion(postWriteForm.getQId());

            result = Post.builder()
                    .postTitle(postWriteForm.getTitle())
                    .postContent(postWriteForm.getContent())
                    .questionType(question.getType())
                    .question(question)
                    .member(member)
                    .build();
        }

        postRepository.save(result);

        setPostCount(result.getPId());

        return result.getPId();
    }

    @Override
    public Long saveQuestionFromPost(Long qId, String userEmail) {
        Question question = questionService.getQuestion(qId);
        bookQuestionService.saveQuestion(qId, userEmail);
        questionService.saveQuestion(question);

        return qId;
    }

    @Override
    public Page<PostResponseForm> getPostList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return makePostPageDTO(pageable, posts);
    }

    @Override
    public Page<PostResponseForm> getFindPostList(Pageable pageable, String keyword, QuestionType type) {
        Page<Post> posts = postRepository.findPostsByQuestionTypeAndTitle(pageable, keyword, type);
        return makePostPageDTO(pageable, posts);
    }

    @Override
    public Page<PostResponseForm> getHotPostList(Pageable pageable) {
        Page<Post> hotPosts = postRepository.findHotPosts(pageable);
        return makePostPageDTO(pageable, hotPosts);
    }

    @Override
    public Page<PostResponseForm> getFindHotPostList(Pageable pageable, String keyword, QuestionType type) {
        Page<Post> posts = postRepository.findHotPostsByQuestionTypeAndTitle(pageable, keyword, type);
        return makePostPageDTO(pageable, posts);
    }

    private Page<PostResponseForm> makePostPageDTO(Pageable pageable, Page<Post> posts) {
        List<PostResponseForm> postResponseForm = new ArrayList<>();

        posts.forEach(post -> postResponseForm.add(new PostResponseForm(post, Long.parseLong(getPostViewCount(post.getPId())))));

        return new PageImpl<>(postResponseForm, pageable, posts.getTotalElements());
    }

    @Override
    public PostDetailForm getPost(Long pId) {
        Post post = postRepository.findByIdWithComments(pId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        return PostDetailForm.builder()
                .post(new PostResponseForm(post, Long.parseLong(getPostViewCount(post.getPId()))))
                .comments(post.getComment().stream().map(CommentResponseForm::new).toList())
                .build();
    }

    @Override
    public PostDetailForm updatePost(Long pId, PostWriteForm postWriteForm) {
        Post post = postRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        post.update(postWriteForm.getTitle(), postWriteForm.getContent());

        return PostDetailForm.builder()
                .post(new PostResponseForm(post, Long.parseLong(getPostViewCount(post.getPId()))))
                .build();
    }

    @Override
    public void removePost(Long pId) {
        Post post = postRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        postRepository.delete(post);
    }

    @Override
    public String setPostCount(Long pId) {

        String viewCount = "post:viewCount:" + pId;
        String upVote = "post:upVote:" + pId;
        String downVote = "post:downVote:" + pId;

        redisTemplate.opsForValue().set(viewCount, "0");
        redisTemplate.opsForValue().set(upVote, "0");
        redisTemplate.opsForValue().set(downVote, "0");

        return redisTemplate.opsForValue().get(viewCount);
    }

    @Override
    public String increasePostViewCount(Long pId, String userEmail) {
        String viewCount = "post:viewCount:" + pId;
        String user = "post:userView:" + pId + ":" + userEmail;

        if (!redisTemplate.hasKey(user)) {
            redisTemplate.opsForValue().increment(viewCount);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(viewCount);
    }

    @Override
    public String getPostViewCount(Long pId) {
        return redisTemplate.opsForValue().get("post:viewCount:" + pId);
    }

    @Override
    public String increasePostVoteCount(Long pId, String userEmail) {

        String upVote = "post:upVote:" + pId;
        String user = "post:userVote:" + pId + ":" + userEmail;

        if (!redisTemplate.hasKey(user)) {
            redisTemplate.opsForValue().increment(upVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        String nowVote = redisTemplate.opsForValue().get(upVote);

        try {
            if (Long.parseLong(nowVote) >= 20) {
                Post post = postRepository.findById(pId)
                        .orElseThrow(() -> new IllegalArgumentException("핫게 등록 실패."));

                post.gettingHot();
                postRepository.save(post);
            }

            return nowVote;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }


    @Override
    public String decreasePostVoteCount(Long pId, String userEmail) {

        String downVote = "post:downVote:" + pId;
        String user = "post:userVote:" + pId + ":" + userEmail;

        if (!redisTemplate.hasKey(user)) {
            redisTemplate.opsForValue().increment(downVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(downVote);
    }

    @Override
    public String getIncreasePostVoteCount(Long pId) {
        return redisTemplate.opsForValue().get("post:upVote:" + pId);
    }

    @Override
    public String getDecreasePostVoteCount(Long pId) {
        return redisTemplate.opsForValue().get("post:downVote:" + pId);
    }


    /**
     * 회원의 게시글 조회 여부를 초기화하는 함수.
     * 1000개 단위로 불러와서, 100개씩 한 번에 삭제하는 batch 처리.
     */
    @Override
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정에 실행
    public void resetPostView() {
        log.info("cron reset post userViewChk");

        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match("post:userView:*")
                .count(1000)
                .build();

        Cursor<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);

        // 1000개씩 읽어서, 100번 읽으면 반영. 그 전에 이미 다 읽으면 그 값을 전부 반영.
        List<String> batchKeys = new ArrayList<>();
        while(keys.hasNext()) {
            batchKeys.add(new String(keys.next()));
            if (batchKeys.size() >= 100) {
                redisTemplate.delete(batchKeys);
                batchKeys.clear();
            }
        }

        if (!batchKeys.isEmpty()) {
            redisTemplate.delete(batchKeys);
        }
    }
}