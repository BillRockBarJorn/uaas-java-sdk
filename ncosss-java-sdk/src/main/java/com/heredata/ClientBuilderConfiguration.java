package com.heredata;

/**
 * <p>Title: ClientBuilderConfiguration</p>
 * <p>Description: 构建客户端连接配置类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/26 11:05
 */
public class ClientBuilderConfiguration extends ClientConfiguration {

    public ClientBuilderConfiguration() {
        super();
        this.supportCname = false;
    }

}
