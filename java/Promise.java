package concurrent.async;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Promise的非线程安全实现
 *
 * <p>
 * Promise是一个纯粹的数据对象，其职责是存储回调函数、存储响应数据；同时做好时序控制，保证触发回调函数无遗漏、保证触发顺序。
 *
 * <p>
 * 异步Promise的特征：
 *     在提交请求时注册回调；
 *     提交请求后，函数立刻返回，不需要等待收到响应；
 *     收到响应后，触发所注册的回调；根据底层实现，可以利用有限数目的线程来接收响应数据，并在这些线程中执行回调
 *
 * <p>
 * Promise的基本方法：
 *     await(listener):void   异步await，注册对响应数据的listener，允许多次调用
 *     signalAll(result):void 通知响应数据result，按顺序触发所注册的listener
 *     await():result         同步await，阻塞，直至获取响应数据再返回
 *
 * <p>
 * Promise的时序：
 *    await(listener) 对 signalAll(result) 可见：注册若干 listener 后，通知result时必须触发每一个listener，不允许遗漏。
 *    signalAll(result) 对 await(listener) 可见：通知 result 后，再注册listener就会立刻触发。
 *    首次 signalAll(result) 对后续 signalAll(result) 可见：
 *        首次通知result后，result即唯一确定，永不改变。之后再通知 result 就会忽略，不产生任何副作用。
 *        请求超时是该特性一种典型应用：在提交请求的同时创建一个定时任务；如果能在超时时长内正确收到响应数据，则通知 Promise正常结束；
 *        否则定时任务超时，通知Promise 异常结束。不论上述事件哪个先发生，都保证只采纳首次通知，使得请求结果唯一确定。
 *    某次 await(listener) 最好对后续 await(listener) 可见，以保证 listener 严格按照注册顺序来触发。
 *
 * @author lihb
 */
public class Promise<T> {

    private boolean isSignaled = false;

    private T result;

    private final List<Consumer<T>> listeners = new LinkedList<>();


    /**
     * 注册一个回调
     *
     * @param listener 回调函数
     */
    public void await(Consumer<T> listener) {
        if (isSignaled) {
            listener.accept(result);
            return;
        }
        listeners.add(listener);
    }

    /**
     * 通知所有回调
     *
     * @param result 结果
     */
    public void signalAll(T result) {
        if (isSignaled) {
            return;
        }

        isSignaled = true;
        this.result = result;
        for (Consumer<T> listener : listeners) {
            listener.accept(result);
        }
        listeners.clear(); // 释放内存，因为listeners 已经触发过，不再需要在内存中存储
    }

    /**
     * 同步获取结果
     *
     * @return 结果
     */
    public T await() {
        // 阻塞，直到signalAll被调用
        while (!isSignaled) {
            Thread.yield();
        }
        return result;
    }
}
