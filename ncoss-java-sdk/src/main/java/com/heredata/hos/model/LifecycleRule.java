package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: LifecycleRule</p>
 * <p>Description: 桶生命周期规则 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LifecycleRule {

    /**
     * <p>Title: Filter</p>
     * <p>Description: 生命周期规则的过滤器。标识适用于生命周期规则的对象 </p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:25
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Filter {
        /**
         * 对象名称的前缀
         */
        private String prefix;
        /**
         * 对象的标签(MD5/哈希值)
         */
        private String tag;
    }

    /**
     * <p>Title: RuleStatus</p>
     * <p>Description: 生命周期的状态 </p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:26
     */
    @AllArgsConstructor
    public static enum RuleStatus {
        Suspended("Suspended", "禁用规则"), Enabled("Enabled", "启用规则");
        private String name;
        private String description;
    }

    /**
     * <p>Title: Expiration</p>
     * <p>Description: 当前版本对象的过期配置 </p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:27
     */
    @Data
    @AllArgsConstructor
    public static class Expiration {
        /**
         * 指定对象过期时间，单位days（天），以对象最后更新时间为准，向后计算过期时间。
         */
        private Integer days;

        /**
         * 指定一个日期，HOS会对最后更新时间早于该日期的数据执行生命周期规则。日期必须服从ISO8601的格式，且要求是UTC的零点。
         * 例：2021-07-23T5:00:00.000 实际北京时间 + 8 为  2021-07-23T13:00:00.000。
         */
        private Date date;

        /**
         * 对象删除标记过期，有效值 ”true ”，与 Days 冲突。
         */
        private Boolean expiredObjectDeleteMarker;

        @Deprecated
        public Expiration(Integer days, Boolean expiredObjectDeleteMarker) {
            this.days = days;
            this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
        }

        public Expiration(Integer days) {
            this.days = days;
        }

        public Expiration() {
        }
    }

    /**
     * <p>Title: AbortIncompleteMultipartUpload</p>
     * <p>Description: 初始化多段对象任务过期时间配置 </p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:28
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AbortIncompleteMultipartUpload {
        /**
         * 初始化多段对象任务过期时间，单位：天
         * 以初始化对象任务最后更新时间为准，向后计算过期时间。
         */
        private Integer daysAfterInitiation;

        public AbortIncompleteMultipartUpload withExpirationDays(int daysAfterInitiation) {
            setDaysAfterInitiation(daysAfterInitiation);
            return this;
        }

        public boolean hasDaysAfterInitiation() {
            return this.daysAfterInitiation != 0;
        }
    }

    /**
     * <p>Title: Transition</p>
     * <p>Description: Object在有效生命周期中, 何时将Object转储为IA、Archive存储类型
     *                Bucket中的Standard Object可以转储为IA或Archive存储类型
     *                但转储Archive存储类型的时间必须比转储IA存储类型的时间长</p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:29
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transition {
        /**
         * 指定对象类型转换时间，单位：天，以对象最后更新时间为准，向后计算类型转换时间。
         */
        private Integer days;

        /**
         *指定一个日期，HOS会对最后更新时间早于该日期的数据执行生命周期规则
         */
        private Date date;

        /**
         * 指定Object转储的存储类型。 {@link StorageClass}
         */
        private StorageClass storageClass;

        public Transition(Integer days, StorageClass storageClass) {
            this.days = days;
            this.storageClass = storageClass;
        }
    }

    /**
     * <p>Title: NoncurrentVersionExpiration</p>
     * <p>Description: 历史版本对象的过期配置 </p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:32
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoncurrentVersionExpiration {
        /**
         * 指定历史版本对象过期时间，单位：天，
         * 以对象最后更新时间为准，向后计算过期时间。
         */
        private Integer noncurrentDays;

        public NoncurrentVersionExpiration withNoncurrentDays(Integer noncurrentDays) {
            setNoncurrentDays(noncurrentDays);
            return this;
        }

        public boolean hasNoncurrentDays() {
            return this.noncurrentDays != null;
        }
    }

    /**
     * <p>Title: NoncurrentVersionTransition</p>
     * <p>Description: 指定Object历史版本在有效生命周期中, 何时将Object 历史版本转储为IA、Archive存储类型 </p>
     * <p>Copyright: Copyright (c) 2022</p>
     * <p>Company: Here-Data </p>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:33
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoncurrentVersionTransition {
        /**
         * 指定历史版本对象转换存储类型时间，单位：天，
         * 以对象最后更新时间为准，向后计算转换时间
         */
        private Integer noncurrentDays;

        public NoncurrentVersionTransition withNoncurrentDays(Integer noncurrentDays) {
            setNoncurrentDays(noncurrentDays);
            return this;
        }
    }

    /**
     * 规则ID，用来标识一条规则,
     * 最长不超过255 字节，同一个配置中，ID不可重复，
     * 如果不指定，对象存储默认生成一个UUID 作为其规则ID
     */
    private String id;
    /**
     * 用于标识适用于生命周期规则的对象。
     * 必须指定{@link Filter#prefix}、{@link Filter#tag}至少一个。
     */
    private Filter filter;
    /**
     * 是否启用或禁用规则
     */
    private RuleStatus status;
    /**
     * 当前版本对象的过期配置 {@link Expiration}
     */
    private Expiration expiration;

    /**
     * 初始化多段对象任务过期时间配置 {@link AbortIncompleteMultipartUpload}
     */
    private AbortIncompleteMultipartUpload abortIncompleteMultipartUpload;

    /**
     * Object在有效生命周期中, 何时将Object转储为IA、Archive存储类型  {@link Transition}
     */
    private List<Transition> transitions = new ArrayList<>();
    /**
     * 历史版本对象的过期配置 {@link NoncurrentVersionExpiration}
     */
    private NoncurrentVersionExpiration noncurrentVersionExpiration;
    /**
     * 指定Object历史版本在有效生命周期中, 何时将Object 历史版本转储为IA、Archive存储类型 {@link NoncurrentVersionTransition}
     */
    private NoncurrentVersionTransition noncurrentVersionTransition;
}
