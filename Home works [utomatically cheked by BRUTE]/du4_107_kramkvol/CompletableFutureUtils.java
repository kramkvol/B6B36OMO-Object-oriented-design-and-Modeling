package cz.cvut.fel.omo.hw.functions.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@UtilityClass
public class CompletableFutureUtils {

    public static <T, U> U applyAndGet(CompletableFuture<T> completableFuture, Function<T, U> applyFnc) {
        // it is not necessary to implement this util function, but I recommend it to you, it will save you a lot of work in the next homework methods
        return null;
    }

}
