package io.github.simonkingws.webconfig.trace.admin.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 注入批量插入和通过主键更新
 *
 * @author: ws
 * @date: 2024/3/6 9:53
 */
@Component
public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatchSomeColumn("insertBatch", null));
        methodList.add(new AlwaysUpdateSomeColumnById("updateByPrimaryKey", null));
        return methodList;
    }
}
