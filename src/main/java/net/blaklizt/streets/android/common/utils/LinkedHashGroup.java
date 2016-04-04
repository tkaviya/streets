package net.blaklizt.streets.android.common.utils;

import android.util.Pair;

import java.util.HashMap;

/**
 * Created by tsungai.kaviya on 2015-11-13.
 */
public class LinkedHashGroup<X, Y, Z> {

    private HashMap<X, Pair<Y, Z>> groupByX = new HashMap<>();
    private HashMap<Y, Pair<X, Z>> groupByY = new HashMap<>();
    private HashMap<Z, Pair<X, Y>> groupByZ = new HashMap<>();

    public void add(X x, Y y, Z z) {
        groupByX.put(x, Pair.create(y, z));
        groupByY.put(y, Pair.create(x, z));
        groupByZ.put(z, Pair.create(x, y));
    }

    public Pair<Y, Z> getByKey1(X key) {
        return groupByX.get(key);
    }

    public Pair<X, Z> getByKey2(Y key) {
        return groupByY.get(key);
    }

    public Pair<X, Y> getByKey3(Z key) {
        return groupByZ.get(key);
    }

    public int size() {
        return groupByX.size();
    }

    public boolean isEmpty() {
        return groupByX.isEmpty();
    }

}
