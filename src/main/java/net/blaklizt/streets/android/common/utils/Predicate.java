package net.blaklizt.streets.android.common.utils;

/******************************************************************************
 * *
 * Created:     28 / 10 / 2015                                             *
 * Platform:    Red Hat Linux 9                                            *
 * Author:      Tich de Blak (Tsungai Kaviya)                              *
 * Copyright:   Blaklizt Entertainment                                     *
 * Website:     http://www.blaklizt.net                                    *
 * Contact:     blaklizt@gmail.com                                         *
 * *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 * *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the         *
 * GNU General Public License for more details.                            *
 * *
 ******************************************************************************/

@FunctionalInterface
public interface Predicate<T> {

//    boolean test(T t);
//
//    default Predicate<T> and(Predicate<? super T> other) {
//        if (other == null) {
//            throw new NullPointerException();
//        }
//        return (t) -> test(t) && other.test(t);
//    }
//
//    default Predicate<T> negate() {
//        return (t) -> !test(t);
//    }
//
//    default Predicate<T> or(Predicate<? super T> other) {
//        if (other == null) {
//            throw new NullPointerException();
//        }
//        return (t) -> test(t) || other.test(t);
//    }
//
//    static <T> Predicate<T> isEqual(Object targetRef) {
//        return (null == targetRef)
//                ? Predicate::isNull
//                : targetRef::equals;
//    }
//
//    static boolean isNull(Object obj) {
//        return obj == null;
//    }
}
