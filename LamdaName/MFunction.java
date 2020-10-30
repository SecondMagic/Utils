import java.io.Serializable;
import java.util.function.Function;

/**
 * @author: SUN
 * @date: 2020/7/22
 * @time: 20:25
 */
@FunctionalInterface
public interface MFunction <T, R> extends Function<T, R>, Serializable {
}
