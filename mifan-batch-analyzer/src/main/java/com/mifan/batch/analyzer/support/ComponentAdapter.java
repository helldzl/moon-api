package com.mifan.batch.analyzer.support;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.amqp.Resource;

import java.util.Map;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/17
 */
public abstract class ComponentAdapter<T> implements Component {

    protected static final Log logger = LogFactory.getLog(ComponentAdapter.class);

    private Publisher publisher;
    private Component component;
    private Map<String, T> map;

    public ComponentAdapter(Publisher publisher, Map<String, T> map) {
        this.publisher = publisher;
        this.map = map;
    }

    @Override
    public void accept(Resource resource) {
        if (component != null) {
            component.accept(resource);
        }
    }

    @Override
    public Component next(Component component) {
        this.component = component;
        return component;
    }

    public T computeIfAbsent(String key, Function<T, T> function) {
        return map.computeIfAbsent(key, name -> {
            T t = newInstance(name);
            if (function == null) {
                return t;
            } else {
                return function.apply(t);
            }
        });
    }

    public T remove(String key) {
        return map.remove(key);
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Component getComponent() {
        return component;
    }

    protected abstract T newInstance(String name);

}
