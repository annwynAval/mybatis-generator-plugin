package com.annwyn.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class OptionalPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType targetJavaType = new FullyQualifiedJavaType("java.util.Optional");
        targetJavaType.addTypeArgument(method.getReturnType());
        method.setReturnType(targetJavaType);

        interfaze.getImportedTypes().add(new FullyQualifiedJavaType("java.util.Optional"));
        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType targetJavaType = new FullyQualifiedJavaType("java.util.Optional");
        targetJavaType.addTypeArgument(method.getReturnType());
        method.setReturnType(targetJavaType);

        topLevelClass.getImportedTypes().add(new FullyQualifiedJavaType("java.util.Optional"));
        return super.clientSelectByPrimaryKeyMethodGenerated(method, topLevelClass, introspectedTable);
    }

}
