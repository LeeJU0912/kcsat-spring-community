package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.redis.RedisKeyUtil;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostWriteForm;
import hpclab.kcsatspringcommunity.community.repository.PostRepository;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import hpclab.kcsatspringcommunity.myBook.service.BookQuestionService;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    @Transactional
    @Override
    public Long savePost(PostWriteForm postWriteForm, String email) {
        Member member = memberService.findMemberByEmail(email);

        String titleHash = Integer.toHexString(postWriteForm.getTitle().hashCode());
        String redisKey = RedisKeyUtil.postIdemCheck(member.getMID(), titleHash);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "locked", Duration.ofMinutes(1));
        if (Boolean.FALSE.equals(success)) {
            throw new ApiException(ErrorCode.DUPLICATE_POST);
        }

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

    @Transactional
    @Override
    public Long saveQuestionFromPost(Long qId, String userEmail) {

        String redisKey = RedisKeyUtil.commentIdemCheck(qId, userEmail);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "locked", Duration.ofMinutes(1));

        if (Boolean.FALSE.equals(success)) {
            throw new ApiException(ErrorCode.DUPLICATE_QUESTION_SAVE);
        }

        Question question = questionService.getQuestion(qId);
        bookQuestionService.saveQuestion(qId, userEmail);
        questionService.saveQuestion(question);

        return qId;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PostResponseForm> getPostList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return makePostPageDTO(pageable, posts);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PostResponseForm> getFindPostList(Pageable pageable, String keyword, QuestionType type) {
        Page<Post> posts = postRepository.findPostsByQuestionTypeAndTitle(pageable, keyword, type);
        return makePostPageDTO(pageable, posts);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PostResponseForm> getHotPostList(Pageable pageable) {
        Page<Post> hotPosts = postRepository.findHotPosts(pageable);
        return makePostPageDTO(pageable, hotPosts);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public Post getPost(Long pId) {
        return postRepository.findByIdWithComments(pId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public PostResponseForm updatePost(Long pId, PostWriteForm postWriteForm) {
        Post post = postRepository.findByIdWithComments(pId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        post.update(postWriteForm.getTitle(), postWriteForm.getContent());

        return new PostResponseForm(post, Long.parseLong(getPostViewCount(post.getPId())));
    }

    @Transactional
    @Override
    public void removePost(Long pId) {
        Post post = postRepository.findById(pId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }

    @Override
    public String setPostCount(Long pId) {

        String viewCount = RedisKeyUtil.postViewCount(pId);
        String upVote = RedisKeyUtil.postUpVote(pId);
        String downVote = RedisKeyUtil.postDownVote(pId);

        redisTemplate.opsForValue().set(viewCount, "0");
        redisTemplate.opsForValue().set(upVote, "0");
        redisTemplate.opsForValue().set(downVote, "0");

        return redisTemplate.opsForValue().get(viewCount);
    }

    @Override
    public String increasePostViewCount(Long pId, String userEmail) {

        String viewCountKey = RedisKeyUtil.postViewCount(pId);
        String userViewKey = RedisKeyUtil.postUserCheck(pId, userEmail);

        if (!redisTemplate.hasKey(userViewKey)) {
            redisTemplate.opsForValue().increment(viewCountKey);
            redisTemplate.opsForValue().set(userViewKey, "1", Duration.ofHours(24));
        }

        return redisTemplate.opsForValue().get(viewCountKey);
    }

    @Override
    public String getPostViewCount(Long pId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.postViewCount(pId));
    }

    @Transactional
    @Override
    public String increasePostVoteCount(Long pId, String userEmail) {

        String upVoteKey = RedisKeyUtil.postUpVote(pId);
        String userVoteKey = RedisKeyUtil.postUserCheck(pId, userEmail);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(userVoteKey, "1", Duration.ofHours(24));

        if (Boolean.TRUE.equals(success)) {
            redisTemplate.opsForValue().increment(upVoteKey);
        }

        String nowVote = redisTemplate.opsForValue().get(upVoteKey);

        try {
            if (nowVote != null && Long.parseLong(nowVote) >= 20) {
                Post post = postRepository.findById(pId)
                        .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

                post.gettingHot();
                postRepository.save(post);
            }

            return nowVote;
        } catch (NumberFormatException e) {
            throw new ApiException(ErrorCode.VOTE_COUNT_PARSE_FAILED);
        }
    }


    @Override
    public String decreasePostVoteCount(Long pId, String userEmail) {

        String downVoteKey = RedisKeyUtil.postDownVote(pId);
        String userVoteKey = RedisKeyUtil.postUserCheck(pId, userEmail);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(userVoteKey, "1", Duration.ofHours(24));

        if (Boolean.TRUE.equals(success)) {
            redisTemplate.opsForValue().increment(downVoteKey);
        }

        return redisTemplate.opsForValue().get(downVoteKey);
    }

    @Override
    public String getIncreasePostVoteCount(Long pId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.postUpVote(pId));
    }

    @Override
    public String getDecreasePostVoteCount(Long pId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.postDownVote(pId));
    }


//    /**
//     * 회원의 게시글 조회 여부를 초기화하는 함수.
//     * 1000개 단위로 불러와서, 100개씩 한 번에 삭제하는 batch 처리.
//     */
//    @Override
//    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정에 실행
//    public void resetPostView() {
//        log.info("cron reset post userViewChk");
//
//        ScanOptions scanOptions = ScanOptions.scanOptions()
//                .match("post:userView:*")
//                .count(1000)
//                .build();
//
//        Cursor<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);
//
//        // 1000개씩 읽어서, 100번 읽으면 반영. 그 전에 이미 다 읽으면 그 값을 전부 반영.
//        List<String> batchKeys = new ArrayList<>();
//        while(keys.hasNext()) {
//            batchKeys.add(new String(keys.next()));
//            if (batchKeys.size() >= 100) {
//                redisTemplate.delete(batchKeys);
//                batchKeys.clear();
//            }
//        }
//
//        if (!batchKeys.isEmpty()) {
//            redisTemplate.delete(batchKeys);
//        }
//    }
}