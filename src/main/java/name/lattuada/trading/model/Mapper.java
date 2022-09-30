package name.lattuada.trading.model;

import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.List;

public final class Mapper {

    private static final ModelMapper MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();
    }

    private Mapper() {
        // Cannot be instantiated
    }

    /**
     * Map object of type "S" (source) to object of type "R" (result).
     *
     * @param source
     * @param resultClass
     * @param <R>
     * @param <S>
     * @return
     */
    public static <R, S> R map(final S source, final Class<R> resultClass) {
        return MODEL_MAPPER.map(source, resultClass);
    }

    /**
     * Map a list of objects having type "S" (source) to a list of objects having type "R" (result).
     *
     * @param sourceList
     * @param resultClass
     * @param <R>
     * @param <S>
     * @return
     */
    public static <R, S> List<R> mapAll(final Collection<S> sourceList, final Class<R> resultClass) {
        return sourceList.stream()
                .map(entity -> map(entity, resultClass))
                .toList();
    }

}
