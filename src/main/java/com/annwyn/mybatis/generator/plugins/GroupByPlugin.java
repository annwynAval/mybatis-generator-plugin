package com.annwyn.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class GroupByPlugin extends PluginAdapter {

    private static final String PROPERTY_COLUMN_NAME = "columnOffset";

    private static final String GROUP_FIELD_COLUMN_NAME = "groupByColumns";
    private static final String GROUP_METHOD_SET_NAME = "setGroupByColumns";
    private static final String GROUP_METHOD_GET_NAME = "setGroupByColumns";

    private static final String HAVING_FIELD_COLUMN_NAME = "havingColumns";
    private static final String HAVING_METHOD_SET_NAME = "setHavingColumns";
    private static final String HAVING_METHOD_GET_NAME = "setHavingColumns";

    /**
     * 此处需要保证group by必须在order by之前, 因此加入偏移量, 在添加XmlElement时指定位置, 一般为负数, 表示最后第N个, 0为最后一个.
     */
    private int columnOffset = -1;

    @Override
    public boolean validate(List<String> warnings) {
        try {
            if(this.properties.containsKey(PROPERTY_COLUMN_NAME)) {
                this.columnOffset = Integer.parseInt(this.properties.getProperty(PROPERTY_COLUMN_NAME));
            }
            return true;
        } catch(NumberFormatException e) {
            warnings.add(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addModelExampleField(topLevelClass, GROUP_FIELD_COLUMN_NAME, GROUP_METHOD_SET_NAME, GROUP_METHOD_GET_NAME);
        this.addModelExampleField(topLevelClass, HAVING_FIELD_COLUMN_NAME, HAVING_METHOD_SET_NAME, HAVING_METHOD_GET_NAME);

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    private void addModelExampleField(TopLevelClass topLevelClass, String columnName, String setMethodName, String getMethodName) {
        Field groupBy = new Field(columnName, FullyQualifiedJavaType.getStringInstance());
        groupBy.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(groupBy);

        Method setGroupBy = new Method(setMethodName);
        setGroupBy.setVisibility(JavaVisibility.PUBLIC);
        setGroupBy.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), columnName));
        setGroupBy.addBodyLine(String.format("this.%s = %s;", columnName, columnName));
        topLevelClass.addMethod(setGroupBy);

        Method getGroupBy = new Method(getMethodName);
        getGroupBy.setVisibility(JavaVisibility.PUBLIC);
        getGroupBy.setReturnType(FullyQualifiedJavaType.getStringInstance());
        getGroupBy.addBodyLine(String.format("return this.%s;", columnName));
        topLevelClass.addMethod(getGroupBy);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.buildGroupElement(element);
        this.buildHavingElement(element);

        return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.buildGroupElement(element);
        this.buildHavingElement(element);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private void buildGroupElement(XmlElement element) {
        XmlElement ifNotNullElement = new XmlElement("if");
        ifNotNullElement.addAttribute(new Attribute("test", String.format("%s != null", GROUP_FIELD_COLUMN_NAME)));
        ifNotNullElement.addElement(new TextElement(String.format("group by ${%s}", GROUP_FIELD_COLUMN_NAME)));

        element.addElement(element.getElements().size() + this.columnOffset, ifNotNullElement);
    }

    private void buildHavingElement(XmlElement element) {
        XmlElement ifNotNullElement = new XmlElement("if");
        ifNotNullElement.addAttribute(new Attribute("test", String.format("%s != null and %s != null", GROUP_FIELD_COLUMN_NAME, HAVING_FIELD_COLUMN_NAME)));
        ifNotNullElement.addElement(new TextElement(String.format("having ${%s}", HAVING_FIELD_COLUMN_NAME)));

        element.addElement(element.getElements().size() + this.columnOffset, ifNotNullElement);
    }

}
