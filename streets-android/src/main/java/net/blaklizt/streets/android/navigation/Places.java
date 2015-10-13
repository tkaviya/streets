/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.blaklizt.streets.android.navigation;

import net.blaklizt.streets.android.R;

import java.util.Random;

public class Places {

    private static final Random RANDOM = new Random();

    public static int getRandomCheeseDrawable() {
        switch (RANDOM.nextInt(5)) {
            default:
            case 0:
                return R.drawable.yams;
            case 1:
                return R.drawable.bag_of_cat;
            case 2:
                return R.drawable.veggies;
            case 3:
                return R.drawable.highlife;
            case 4:
                return R.drawable.beer;
        }
    }

    public static final String[] sCheeseStrings = {
            "Yams", "Support", "Church", "Snow", "Wave", "Mary J."
    };

}
