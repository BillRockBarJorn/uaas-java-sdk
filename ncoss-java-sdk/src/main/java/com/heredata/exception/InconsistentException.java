package com.heredata.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Title: InconsistentException</p>
 * <p>Description: 不一致异常，当CRC循环冗余校验不通过后会抛出此异常 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:57
 */
@Data
@AllArgsConstructor
public class InconsistentException extends RuntimeException {

    private static final long serialVersionUID = 2140587868503948665L;

    private Long clientChecksum;
    private Long serverChecksum;
    private String requestId;


    @Override
    public String getMessage() {
        return "InconsistentException " + "\n[RequestId]: " + getRequestId() + "\n[ClientChecksum]: "
                + getClientChecksum() + "\n[ServerChecksum]: " + getServerChecksum();
    }

}
