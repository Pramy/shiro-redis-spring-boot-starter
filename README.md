# shiro-redis-spring-boot-starter
```xml
<dependency>
   <groupId>com.pramyness</groupId>
   <artifactId>shiro-redis-spring-boot-starter</artifactId>
   <version>${version}</version>
</dependency>
```

- Use kryo as redis serialization

# properties

- prefix = "spring.shiro.redis"

```java
private String keyPrefix；

private String sessionPrefix；
//default:30 minute，time unit：millis
private Long sessionTimeOut ；
//default:30 minute，time unit：millis
private Long sessionCacheExpire ；
//default:-1
private Long valueCacheExpire ；
// kryo serialize for transient field,default:true
private boolean isSerializeTransient；
```

