package hpclab.kcsatspringcommunity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse {
    private String code;
    private String message;
}