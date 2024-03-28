package io.github.simonkingws.webconfig.trace.admin;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.Collections;

/**
 * TODO
 *
 * @author: ws
 * @date: 2024/3/5 16:10
 */
public class MPPGeneratorTest {

    @Test
    public void test() {
        FastAutoGenerator.create("jdbc:mysql://10.100.210.35:3306/webconfig_trace?characterEncoding=utf-8", "autostreets", "autostreets")
                .globalConfig(builder -> {
                    builder.author("ws").disableOpenDir().outputDir("src/test/java"); // 指定输出目录
                })

                // 数据库相关的配置
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.BOOLEAN;
                    }
                    if (typeCode == Types.DATE || typeCode == Types.TIMESTAMP) {
                        return DbColumnType.DATE;
                    }

                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("com.simonkingws.webconfig.trace.admin") // 设置父包名
                            //.moduleName("webconfig") // 设置父包模块名
                            .entity("model")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "src/test/java/xml")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("trace_walking_compete", "trace_walking_method", "trace_walking_server", "trace_walking_user")
                    //.entityBuilder().enableLombok().enableFileOverride().enableRemoveIsPrefix().enableTableFieldAnnotation().enableTableFieldAnnotation()
                    .entityBuilder().enableLombok().enableFileOverride().enableRemoveIsPrefix()
                    .serviceBuilder().enableFileOverride().formatServiceFileName("%sService")
                    .mapperBuilder().enableFileOverride()
                    .controllerBuilder().enableFileOverride();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                // 模板引擎的控制
                .templateConfig(builder -> {
                    // 不生成Controller
                    builder.controller("");
                })
                .execute();

        System.out.println("方法执行完成......");

    }
}
