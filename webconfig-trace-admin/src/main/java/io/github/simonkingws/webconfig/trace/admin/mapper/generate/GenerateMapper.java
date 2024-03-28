package io.github.simonkingws.webconfig.trace.admin.mapper.generate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import io.github.simonkingws.webconfig.trace.admin.config.CustomSqlInjector;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定制公用sql注入
 *
 * <pre>
 *   insetBatch 方法通过{@link InsertBatchSomeColumn} 注入
 *   updateByPrimaryKey 方法通过{@link AlwaysUpdateSomeColumnById} 注入
 * </pre>
 * @see CustomSqlInjector customsqlInjector
 *
 * @author: ws
 * @date: 2023/9/7 13:58
 */
public interface GenerateMapper<T> extends BaseMapper<T> {

    void insertBatch(@Param(Constants.LIST) List<T> entityList);

    int updateByPrimaryKey(@Param(Constants.ENTITY) T entity);
}
