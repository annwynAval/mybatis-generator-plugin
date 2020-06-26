# mybatis-generator-plugin
mybatis-generator用于生成mybatis的PO类, Mapper接口以及xml文件.
其默认提供的插件功能比较少, 在[itfsw/mybatis-generator-plugin](https://github.com/itfsw/mybatis-generator-plugin)中扩展了很多功能, 本项目也是用于扩展mybatis-generator. 提供`itfsw/mybatis-generator-plugin`中没有的功能.

1. GroupBy扩展
2. 返回Optional对象
 
##### 该插件暂时只支持mybatis-generator版本1.3.7以下.
##### 该插件暂时只支持mysql数据库, 其他数据库暂时不支持.
##### Optional对象需要保证mybatis版本高于3.5

## 使用方式
clone本项目之后, 执行`mvn clean install`.

```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>1.3.7</version>
    <configuration>
        <configurationFile>${mybatis.generator.location}</configurationFile>
        <overwrite>true</overwrite>
        <verbose>true</verbose>
    </configuration>
    <dependencies>
        <dependency> <!-- github上, mybatis-generator的第三方插件扩展. 不需要可以删除 -->
            <groupId>com.itfsw</groupId>
            <artifactId>mybatis-generator-plugin</artifactId>
            <version>1.3.7</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <version>1.3.7</version>
        </dependency>
        <dependency>
            <groupId>com.annwyn.mybatis</groupId>
            <artifactId>mybatis-generator-plugin</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

## GroupByPlugin
为example类添加groupByColumns与havingColumns属性. 

##### 使用方式
```xml
<generatorConfiguration>
    <context id="context" targetRuntime="MyBatis3">
        <plugin type="com.annwyn.mybatis.generator.plugins.GroupByPlugin" >
            <property name="columnOffset" value="-1"/>
        </plugin>
    </context>
</generatorConfiguration>
```
该插件下可以指定参数columnOffset, 用于指定group by标签的生成位置. 默认为-1, 表示添加至最后第二个.
在使用该plugin时需要注意插件添加的顺序, 该顺序会影响xml最终生成结果, 可能会导致group by, order by和limit语句的位置不正确.

##### 生成结果
为xml中selectByExample与selectByExampleWithBLOBs方法中添加如下语句
```xml
<select id="selectByExampleWithBLOBs" parameterType="com.annwyn.soter.mybatis.generator.example.PostDetailExample" resultMap="ResultMapWithBLOBs">
    <!--
      WARNING - @mbg.generated
      This element is automatically generated by MyBatis Generator, do not modify.
    -->
    select
    <if test="distinct">
      distinct
    </if>
    <include refid="Base_Column_List" />
    ,
    <include refid="Blob_Column_List" />
    from soter_post_detail
    <if test="_parameter != null">
      <include refid="Example_Where_Clause" />
    </if>
    <if test="groupByColumns != null">
      group by ${groupByColumns}
    </if>
    <if test="groupByColumns != null and havingColumns != null">
      having ${havingColumns}
    </if>
    <if test="orderByClause != null">
      order by ${orderByClause}
    </if>
    <if test="rows != null">
      <if test="offset != null">
        limit ${offset}, ${rows}
      </if>
      <if test="offset == null">
        limit ${rows}
      </if>
    </if>
</select>
```

## 返回Optional对象
将Mapper接口中selectByPrimaryKey返回值替换成Optional对象. 使用本插件需要保证mybatis版本高于3.5

##### 使用方式
```xml
<generatorConfiguration>
    <context id="context" targetRuntime="MyBatis3">
        <plugin type="com.annwyn.mybatis.generator.plugins.OptionalPlugin" />
    </context>
</generatorConfiguration>
```

##### 生成结果
```java
public interface PostMapper {
    
    Optional<Post> selectByPrimaryKey(Integer postId);

}
```