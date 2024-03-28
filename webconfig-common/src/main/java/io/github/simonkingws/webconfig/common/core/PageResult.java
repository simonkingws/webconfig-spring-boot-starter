package io.github.simonkingws.webconfig.common.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页参数
 *
 * @author: ws
 * @date: 2024/1/31 14:24
 */
@Getter
@Setter
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 6812276888950275942L;

    /**
     * 分页默认长度
     */
    public static final Integer DEFAULT_PAGE_LENGTH = 10;

    /**
     * 当前页数
     */
    private int current = 1;

    /**
     * 起始位置
     */
    private int begin = 0;

    /**
     * 结束位置
     */
    private int end;

    /**
     * 分页大小
     */
    private int length;

    /**
     * 总记录数
     */
    private int count;


    /**
     * 总页数
     */
    private int total;

    /**
     * 分页数据
     */
    private List<T> dataList;

    /**
     * 空参构造
     *
     * @author ws
     * @date 2024/1/31 14:29
     */
    public PageResult() {
        this.length = DEFAULT_PAGE_LENGTH;
    }


    /**
     * 设置页面长度
     *
     * @author ws
     * @date 2024/1/31 14:31
     */
    public PageResult(int length) {
        this.length = length;
    }

    /**
     * 返回总记录数和数据
     *
     * @author ws
     * @date 2024/1/31 14:40
     */
    public static <T> PageResult<T> of(int count, List<T> list) {
        PageResult<T> rs = new PageResult<>();
        rs.setCount(count);
        rs.setDataList(list);
        return rs;
    }

    /**
     * 空参构造
     *
     * @author ws
     * @date 2024/1/31 14:37
     */
    public static <T> PageResult<T> emptyPage() {
        return new PageResult<>();
    }

    public int getEnd() {
        return this.end = this.begin + this.length;
    }

    public void setLength(int length) {
        this.length = length;
        this.begin = (this.current - 1) * this.length;
        this.end = this.begin + this.length;
    }

    public void setCount(int count) {
        this.count = count;
        this.begin = (this.current - 1) * this.length;
        this.end = this.begin + this.length;
        this.total = (int)Math.floor((double)this.count * 1.0D / (double)this.length);
        if (this.count % this.length != 0) {
            ++this.total;
        }

        if (this.current > this.total) {
            this.setCurrent(1);
        }
    }

    public void setCurrent(int current) {
        this.current = current;
        this.begin = (this.current - 1) * this.length;
        this.end = this.begin + this.length;
    }

    public int getTotal() {
        return this.total == 0 ? 1 : this.total;
    }

    public boolean isFirstPage() {
        return this.current == 1;
    }

    public boolean isLastPage() {
        return this.current == this.total;
    }
}
