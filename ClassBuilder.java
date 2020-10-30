import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author: SUN
 * @date: 2020/8/27
 * @time: 15:15
 * @Desc 对象创建初始化工具
 */
public class ClassBuilder<T> {
    private final Supplier<T> instantiator;

    private List<Consumer<T>> modifiers = new ArrayList<>();

    public ClassBuilder(Supplier<T> instantiator) {
        this.instantiator = instantiator;
    }

    public static <T> ClassBuilder<T> of(Supplier<T> instantiator) {
        return new ClassBuilder<>(instantiator);
    }

    public <P> ClassBuilder<T> with(ConsumerI<T, P> consumerI, P p) {
        Consumer<T> consumer = s -> consumerI.accept(s, p);
        modifiers.add(consumer);

        return this;
    }

    public <P1, P2> ClassBuilder<T> with(ConsumerII<T, P1, P2> consumerI, P1 p1, P2 p2) {
        Consumer<T> consumer = s -> consumerI.accept(s, p1, p2);
        modifiers.add(consumer);

        return this;
    }

    public <P1, P2, P3> ClassBuilder<T> with(ConsumerIII<T, P1, P2, P3> consumerII, P1 p1, P2 p2, P3 p3) {
        Consumer<T> consumer = s -> consumerII.accept(s, p1, p2, p3);
        modifiers.add(consumer);

        return this;
    }

    public T build() {
        T t = instantiator.get();
        modifiers.forEach(s -> {
            s.accept(t);
        });
        modifiers.clear();
        return t;
    }

    /**
     * 单参数方法
     *
     * @param <T>
     * @param <P>
     */
    @FunctionalInterface
    public interface ConsumerI<T, P> {
        void accept(T t, P p);
    }

    /**
     * 双参数方法
     *
     * @param <T>
     * @param <P1>
     * @param <P2>
     */
    @FunctionalInterface
    public interface ConsumerII<T, P1, P2> {
        void accept(T t, P1 p1, P2 p2);
    }

    @FunctionalInterface
    public interface ConsumerIII<T, P1, P2, P3> {
        void accept(T t, P1 p1, P2 p2, P3 p3);
    }

    /**
     * ......如上以此类推
     */
}
