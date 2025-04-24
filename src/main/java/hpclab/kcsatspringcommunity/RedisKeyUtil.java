package hpclab.kcsatspringcommunity;


/**
 * Redis DB와 상호작용하는 키를 통합 관리하는 저장소 클래스입니다.
 */
public class RedisKeyUtil {

    // 댓글 관련 키
    public static String commentUpVote(Long cId) {
        return "comment:" + cId + ":upVote";
    }

    public static String commentDownVote(Long cId) {
        return "comment:" + cId + ":downVote";
    }

    public static String commentUserCheck(Long cId, String email) {
        return "comment:" + cId + ":user:" + email;
    }

    public static String commentIdemCheck(Long mId, String hash) {
        return "comment:lock:" + mId + ":" + hash;
    }

    // 게시글 관련 키
    public static String postViewCount(Long pId) {
        return "post:viewCount:" + pId;
    }

    public static String postUpVote(Long pId) {
        return "post:upVote:" + pId;
    }

    public static String postDownVote(Long pId) {
        return "post:downVote:" + pId;
    }

    public static String postUserCheck(Long pId, String email) {
        return "post:userView:" + pId + ":" + email;
    }

    public static String postIdemCheck(Long mId, String hash) {
        return "post:lock:" + mId + ":" + hash;
    }

    // 문제 관련 키
    public static String questionSavedCheck(String email, Long qId) {
        return "question:" + email + ":isSaved:" + qId;
    }

    public static String questionRank(int rank) {
        return "question:rank:" + rank;
    }

    //JWT 관련 키


    // 회원 관련 키
    public static String userIdemCheck(String email) {
        return "signup:lock:" + email;
    }
}
