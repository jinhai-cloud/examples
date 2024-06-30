package com.examples.mlops;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class F {

    public static <T> CompletableFuture<T> callAsync(String code, Map<String, Object> args) {
        // 根据code，获取特征的metadata
        Metadata meta = getFeatureMeta(code);

        // 根据metadata的type，获取对应的执行器。sql时，不同数据源对应不同执行器对象
        Query query = getFeatureQuery(meta.getType);

        QueryParms param = buildQueryParam();

        // meta包含sql、script、url以及参数等信息。还是说 封装一个queryParam对象？
        return query.queryForObject(param);

        其实就需要两个东西：特征的数据源类型，以及执行具体数据源时依赖的请求参数。可以一个接口，实现这两个参数

        return null;
    }

    public static <T> T call(String code, Map<String, Object> args) {
        return null;
    }
}
