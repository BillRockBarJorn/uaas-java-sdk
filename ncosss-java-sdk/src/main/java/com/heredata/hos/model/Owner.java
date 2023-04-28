package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The entity class representing the owner of HOS {@link Bucket}.
 *
 */

/**
 * <p>Title: Owner</p>
 * <p>Description: 拥有者实体，即属于哪一个账户 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Owner implements Serializable {

    private static final long serialVersionUID = -1942759024112448066L;
    /**
     * 账户ID
     */
    private String id;
}
