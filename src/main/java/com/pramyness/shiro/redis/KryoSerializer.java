package com.pramyness.shiro.redis;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.web.util.SavedRequest;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;
import java.util.Map;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/10.
 */
public class KryoSerializer<T> implements RedisSerializer<T> {

    private final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static ThreadLocal<Kryo> KRYO_THREAD_LOCAL;

    public KryoSerializer(final boolean  serializeForTransient, final List<Class<?>> list) {
        KRYO_THREAD_LOCAL = new ThreadLocal<Kryo>(){
            @Override
            protected Kryo initialValue() {
                return init(serializeForTransient,list);
            }
        };
    }


    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            logger.debug("serialize object is null");
            return null;
        }
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        Output output = new Output(4096, -1);
        kryo.writeClassAndObject(output, t);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            logger.debug("deserialize object is null");
            return null;
        }
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        Input input = new Input(bytes);
        Object o = kryo.readClassAndObject(input);
        input.close();
        return (T) o;
    }

    private Kryo init(boolean serializeForTransient,List<Class<?>>list){
        final Kryo kryo = new Kryo();
        kryo.getFieldSerializerConfig().setSerializeTransient(serializeForTransient);
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(SimpleSession.class);
        kryo.register(SavedRequest.class);
        for (Class<?> aClass : list) {
            kryo.register(aClass);
        }
        return kryo;
    }
}
