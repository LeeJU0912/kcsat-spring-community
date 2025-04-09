package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostDetailForm;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostWriteForm;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.community.repository.PostRepository;
import hpclab.kcsatspringcommunity.myBook.service.BookQuestionService;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
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
    private final MemberRepository memberRepository;
    private final QuestionJPARepository questionJPARepository;
    private final BookQuestionService bookQuestionService;

    private final RedisTemplate<String, String> redisTemplate;


    // 게시글 저장
    @Override
    public Long savePost(PostWriteForm postWriteForm, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));
        Post result;

        if (postWriteForm.getQId() == null) {
            result = Post.builder()
                    .postTitle(postWriteForm.getTitle())
                    .postContent(postWriteForm.getContent())
                    .member(member)
                    .build();
        }
        else {
            Question question = questionJPARepository.findById(postWriteForm.getQId()).orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));

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
    public boolean saveQuestionFromPost(Long qId, String userEmail) {
        Question question = questionJPARepository.findById(qId).orElseThrow(() -> new IllegalArgumentException("question not found"));
        bookQuestionService.saveQuestion(qId, userEmail);
        questionJPARepository.save(question);

        return true;
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
    public PostDetailForm getPost(Long postId) {
        Post post = postRepository.findByIdWithComments(postId);
        return PostDetailForm.builder()
                .post(new PostResponseForm(post, Long.parseLong(getPostViewCount(post.getPId()))))
                .comments(post.getComment().stream().map(comment -> new CommentResponseForm()).toList())
                .build();
    }

    @Override
    public PostDetailForm updatePost(Long postId, PostWriteForm postWriteForm) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        post.update(postWriteForm.getTitle(), postWriteForm.getContent());

        return PostDetailForm.builder()
                .post(new PostResponseForm(post, Long.parseLong(getPostViewCount(post.getPId()))))
                .build();
    }

    @Override
    public void removePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        postRepository.delete(post);
    }

    @Override
    public String setPostCount(Long postId) {

        String viewCount = "post:viewCount:" + postId;
        String upVote = "post:upVote:" + postId;
        String downVote = "post:downVote:" + postId;

        redisTemplate.opsForValue().set(viewCount, "0");
        redisTemplate.opsForValue().set(upVote, "0");
        redisTemplate.opsForValue().set(downVote, "0");

        return redisTemplate.opsForValue().get(viewCount);
    }

    @Override
    public String increasePostViewCount(Long postId, String userEmail) {
        String viewCount = "post:viewCount:" + postId;

        String user = "post:userView:" + postId + ":" + userEmail;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(user))) {
            redisTemplate.opsForValue().increment(viewCount);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(viewCount);
    }

    @Override
    public String getPostViewCount(Long postId) {
        return redisTemplate.opsForValue().get("post:viewCount:" + postId);
    }

    @Override
    public String increasePostVoteCount(Long postId, String userEmail) {

        String upVote = "post:upVote:" + postId;
        String user = "post:userVote:" + postId + ":" + userEmail;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(user))) {
            redisTemplate.opsForValue().increment(upVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        String nowVote = redisTemplate.opsForValue().get(upVote);

        if (Integer.parseInt(nowVote) >= 20) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("핫게 등록 실패."));
            post.gettingHot();

            postRepository.save(post);
        }

        return nowVote;
    }


    @Override
    public String decreasePostVoteCount(Long postId, String userEmail) {

        String downVote = "post:downVote:" + postId;
        String user = "post:userVote:" + postId + ":" + userEmail;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(user))) {
            redisTemplate.opsForValue().increment(downVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(downVote);
    }

    @Override
    public String getIncreasePostVoteCount(Long postId) {
        return redisTemplate.opsForValue().get("post:upVote:" + postId);
    }

    @Override
    public String getDecreasePostVoteCount(Long postId) {
        return redisTemplate.opsForValue().get("post:downVote:" + postId);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정에 실행
    public void resetPostView() {
        log.info("cron reset post userViewChk");

        ScanOptions scanOptions = ScanOptions.scanOptions().match("post:userView:*").count(1000).build();

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