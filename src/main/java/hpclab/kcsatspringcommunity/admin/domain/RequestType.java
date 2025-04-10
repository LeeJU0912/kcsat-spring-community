package hpclab.kcsatspringcommunity.admin.domain;

/**
 * <p>회원 요청 사항에 대한 유형을 구별하는 열거형 클래스입니다.</p>
 * <p>유형 종류</p>
 * <ul>
 *     <li>문제 오류</li>
 *     <li>건의 사항</li>
 *     <li>그 외</li>
 * </ul>
 */
public enum RequestType {

    QUESTION_ERROR,
    IMPROVING,
    ETC
}
