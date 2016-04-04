package net.blaklizt.streets.android.common.utils;

public class Try<T, K> {

    private final T value;
    private final K error;

    private Try(T value, K error) {
        this.value = value;
        this.error = error;
    }

    @SuppressWarnings("unchecked")
    public static <T, K extends RuntimeException> Try<T, K> attempt(Supplier<T> supplier) {
        try {
            return (Try<T, K>) new Try(supplier.get(), null);
        } catch (RuntimeException e) {
            return (Try<T, K>) new Try(null, e);
        }
    }

    public Optional<T> get() { return Optional.ofNullable(value); }

    public Optional<K> error() { return Optional.ofNullable(error); }

    @SuppressWarnings("unchecked")
    public static <T, K> Try<T, K> either(boolean condition, T success, K failure) {
        return (Try<T, K>) ((condition) ? new Try<>(success, null) : new Try<>(null, failure));
    }

    @SuppressWarnings("unchecked")
    public static <T, K> Try<T, K> lazyEither(boolean condition, Supplier<T> success, Supplier<K> failure) {
        return (Try<T, K>) ((condition) ? new Try<>(success.get(), null) : new Try<>(null, failure.get()));
    }

//    public <Z> Z map(Function<T, Z> onSuccess, Function<K, Z> onFailure) {
//        return (this.isSuccess()) ? onSuccess.apply(this.value) : onFailure.apply(this.error);
//    }

    public boolean isSuccess() { return get().isPresent(); }

    public boolean isFailure() { return error().isPresent(); }

    public static <T, K> Try<T, K> fail(K error) { return new Try<>(null, error); }

    //this unused parameters is sometimes necessary to give the type inference a hope
    public static <T, K> Try<T, K> fail(K error, Class<T> type) { return new Try<>(null, error); }

    public static <T, K> Try<T, K> success(T val) { return new Try<>(val, null); }

    public static <T, K> Try<T, K> success(T val, Class<K> type) { return new Try<>(val, null); }

    public T value() { return value; }

//    public  Try<T,K> onSuccess(Function<T,Try<T,K>> function) { return isSuccess() ?  function.apply(value()) : this; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Try{");
        sb.append("value=").append(value);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
