import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: SUN
 * @date: 2020/10/29
 * @time: 17:24
 * 通用排序工具
 */
public class CompareUtils {
    public final static String dataType_String = "String";
    public final static String dataType_Number = "Number";
    public final static String dataType_Percent = "Percent";
    public final static String dataType_Date = "Date";

    public final static String sortType_Desc = "desc";
    public final static String sortType_Asc = "asc";

    public final static Map<String,String> dataTypeMap = new HashMap<>();

    static{
        dataTypeMap.put(String.class.getName(),dataType_String);
        dataTypeMap.put(boolean.class.getName(),dataType_String);
        dataTypeMap.put(Boolean.class.getName(),dataType_String);
        dataTypeMap.put(char.class.getName(),dataType_String);
        dataTypeMap.put(Double.class.getName(),dataType_Number);
        dataTypeMap.put(double.class.getName(),dataType_Number);
        dataTypeMap.put(Float.class.getName(),dataType_Number);
        dataTypeMap.put(float.class.getName(),dataType_Number);
        dataTypeMap.put(Integer.class.getName(),dataType_Number);
        dataTypeMap.put(int.class.getName(),dataType_Number);
        dataTypeMap.put(Long.class.getName(),dataType_Number);
        dataTypeMap.put(long.class.getName(),dataType_Number);
        dataTypeMap.put(byte.class.getName(),dataType_Number);
        dataTypeMap.put(short.class.getName(),dataType_Number);
        dataTypeMap.put(BigDecimal.class.getName(),dataType_Number);
        dataTypeMap.put(Date.class.getName(),dataType_Date);
    }

    public static <T> void compare(List<T> list, List<SortBy> sortBys) {
        if(CollectionUtils.isEmpty(list)){
            return ;
        }

        Map<String,String> fieldMap = Optional.ofNullable(list.get(0).getClass().getDeclaredFields())
                .map(s->Arrays.asList(s)).orElseGet(ArrayList::new).stream()
                .collect(Collectors.toMap(s->s.getName(),s->
                        Optional.ofNullable(dataTypeMap.get(s.getType().getName())).orElseGet(()->dataType_String)));

        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                Map<String, String> map1 = new HashMap<>();
                Map<String, String> map2 = new HashMap<>();
                try {
                    map1 = BeanUtils.describe(o1);
                    map2 = BeanUtils.describe(o2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (SortBy sortBy : sortBys) {
                    String obj1 = map1.get(sortBy.getColumnName());
                    String obj2 = map2.get(sortBy.getColumnName());
                    Boolean asc = sortType_Asc.equals(sortBy.getType());

                    if (obj1 == null && obj2 == null) {
                        return 0;
                    } else if (obj1 != null && obj2 == null) {
                        return sortBy.getNullLast() ? -1 : 1;
                    } else if (obj1 == null && obj2 != null) {
                        return sortBy.getNullLast() ? 1 : -1;
                    } else {
                        int result;
                        if(StringUtils.isEmpty(sortBy.getDataType())){
                            Consumer<SortBy> data = z -> ((ConsumerM<SortBy, String>) (SortBy::setDataType))
                                    .accept(z, Optional.ofNullable(fieldMap.get(sortBy.getColumnName())).orElseGet(()->dataType_String));
                            data.accept(sortBy);
                        }

                        switch (sortBy.getDataType()) {
                            case dataType_String:
                                result = obj1.compareTo(obj2);
                                if (result != 0) {
                                    return asc ? result : obj2.compareTo(obj1);
                                }
                                break;
                            case dataType_Number:
                            case dataType_Percent:
                                Double d1 = Double.parseDouble(obj1.replaceAll("%", ""));
                                Double d2 = Double.parseDouble(obj2.replaceAll("%", ""));

                                result = Double.compare(d1, d2);
                                if (result != 0) {
                                    return asc ? result : Double.compare(d2, d1);
                                }
                                break;
                            case dataType_Date:
                                Date date1 = new Date(obj1);
                                Date date2 = new Date(obj2);
                                result = Long.compare(date1.getTime(), date2.getTime());
                                if (result != 0) {
                                    return asc ? result : Long.compare(date2.getTime(), date1.getTime());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }

                return 0;
            }
        });
    }

    public static <T> void compareAsc(List<T> list, CFunction<T, ?>... lambdas) {
        if(lambdas == null || lambdas.length <= 0){
            return;
        }
        compare(list, Arrays.asList(lambdas).stream()
                .map(lambda->
                        createSortBy(getName(lambda), null, sortType_Asc, false)
                ).collect(Collectors.toList()));
    }

    public static <T> void compareAscNullLast(List<T> list, CFunction<T, ?>... lambdas) {
        if(lambdas == null || lambdas.length <= 0){
            return;
        }
        compare(list, Arrays.asList(lambdas).stream()
                .map(lambda->
                        createSortBy(getName(lambda), null, sortType_Asc, true)
                ).collect(Collectors.toList()));
    }

    public static <T> void compareDesc(List<T> list, CFunction<T, ?>... lambdas) {
        if(lambdas == null || lambdas.length <= 0){
            return;
        }
        compare(list, Arrays.asList(lambdas).stream()
                .map(lambda->
                        createSortBy(getName(lambda), null, sortType_Desc, false)
                ).collect(Collectors.toList()));
    }

    public static <T> void compareDescNullLast(List<T> list, CFunction<T, ?>... lambdas) {
        if(lambdas == null || lambdas.length <= 0){
            return;
        }
        compare(list, Arrays.asList(lambdas).stream()
                .map(lambda->
                        createSortBy(getName(lambda), null, sortType_Desc, true)
                ).collect(Collectors.toList()));
    }

    private static SortBy createSortBy(String columnName, String dataType, String type, Boolean isNullLast) {
        SortBy o = ((Supplier<SortBy>) (SortBy::new)).get();
        Consumer<SortBy> consumer = s -> ((ConsumerM<SortBy, String>) (SortBy::setColumnName))
                .accept(s, columnName);
        consumer.accept(o);
        consumer = s -> ((ConsumerM<SortBy, String>) (SortBy::setDataType))
                .accept(s, dataType);
        consumer.accept(o);
        consumer = s -> ((ConsumerM<SortBy, String>) (SortBy::setType))
                .accept(s, type);
        consumer.accept(o);
        consumer = s -> ((ConsumerM<SortBy, Boolean>) (SortBy::setNullLast))
                .accept(s, isNullLast);
        consumer.accept(o);
        return o;
    }

    public static class SortBy {
        private String columnName;
        private String dataType;
        private String type = sortType_Asc;
        private Boolean isNullLast = false;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getNullLast() {
            return isNullLast;
        }

        public void setNullLast(Boolean nullLast) {
            isNullLast = nullLast;
        }
    }

    @FunctionalInterface
    public interface ConsumerM<T, P> {
        void accept(T t, P p);
    }

    @FunctionalInterface
    public interface CFunction <T, R> extends Function<T, R>, Serializable {
    }

    public static <T> String getName(CFunction<T, ?> lambda) {
        try {
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            String getter = serializedLambda.getImplMethodName();
            String fieldName = Introspector.decapitalize(getter.replace("get", ""));
            return fieldName;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
