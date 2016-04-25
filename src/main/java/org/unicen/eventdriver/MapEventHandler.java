package org.unicen.eventdriver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MapEventHandler implements EventHandler {

    private Map<Object, Set<EventListener>> listeners = new HashMap<Object, Set<EventListener>>();

    public void subscribeListener(Object provider, EventListener listener) {

        Objects.requireNonNull(provider);
        Objects.requireNonNull(listener);

        Set<EventListener> subscribers = listeners.get(provider);
        if (subscribers == null) {
            subscribers = new HashSet<EventListener>();
            listeners.put(provider, subscribers);
        }

        subscribers.add(listener);
    }

    /**
     * Creates a provider instance wired to this handler, by wrapping the
     * interface and delegating to the class.
     * 
     * @param implClass
     * @return T extends EventProvider<?>
     */
    public <T extends EventListener> T createProvider(Object provider, Class<T> listenerInterface) {

        // TODO: Check the interface & implementation -> listener + event
        // provider

        Set<EventListener> listenerSubscribers = listeners.get(provider);
        if (listenerSubscribers == null) {
            listenerSubscribers = Collections.emptySet();
        }

        EventInvocationHandler eventInvocationHandler = new EventInvocationHandler(listenerSubscribers);

        Class<?>[] interfaces = new Class<?>[] { listenerInterface };

        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, eventInvocationHandler);

        return proxy;
    }
    
    private static class EventInvocationHandler implements InvocationHandler {

        private final ExecutorService executorService;
        private final Set<EventListener> subscribers;

        public EventInvocationHandler(Set<EventListener> subscribers) {

            this.subscribers = subscribers;
            this.executorService = (!subscribers.isEmpty()) ? Executors.newFixedThreadPool(subscribers.size(), threadFactory) : null;
        }

        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {

            for (final EventListener listener : subscribers) {

                executorService.execute(new Runnable() {

                    public void run() {
                        try {
                            method.invoke(listener, args);
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }
                });
            }

            return null;
        }
    }
    
    private static final ThreadFactory threadFactory = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {

            Thread eventThread = new Thread(r);
            eventThread.setUncaughtExceptionHandler(eh);

            return eventThread;
        }
    };
    
    private static final Thread.UncaughtExceptionHandler eh = new Thread.UncaughtExceptionHandler() {

        public void uncaughtException(Thread th, Throwable ex) {
            System.out.println("Uncaught exception: " + ex);
        }
    };
}
