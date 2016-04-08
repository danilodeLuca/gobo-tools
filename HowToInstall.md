# Step 1 #

Download the latest version of zip file from http://code.google.com/p/gobo-tools/downloads/list.

You can see files below in zip.

  * gobo-tools.xxxxxxxx.jar
  * gdata-media-1.0.jar
  * gdata-docs-3.0.jar
  * google-collect-1.0-rc1.jar
  * gdata-spreadsheet-meta-3.0.jar
  * gdata-spreadsheet-3.0.jar
  * gdata-core-1.0.jar
  * gdata-client-meta-1.0.jar
  * gdata-client-1.0.jar
  * gobo（Folder）

# Step 2 #

Copy all jar files under war/WEB-INF/lib

Copy the gobo folder under war

# Step 3 #

Add the expressions below to web.xml

```
    <servlet>
        <servlet-name>GoboServlet</servlet-name>
        <servlet-class>gobo.GoboServlet</servlet-class>
    </servlet>
```

```
    <servlet-mapping>
        <servlet-name>GoboServlet</servlet-name>
        <url-pattern>*.gobo</url-pattern>
    </servlet-mapping>
```

```
    <security-constraint>
        <web-resource-collection>
            <url-pattern>*.gobo</url-pattern>
        </web-resource-collection>
        <auth-constraint>
            <role-name>admin</role-name>
        </auth-constraint>
    </security-constraint>
```

**That's all**

Now, You can use tools at http(s)://your-app-id.appspot.com/index.gobo **(The extension is "gobo")**