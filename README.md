# AutoScrollViewPager

[![Release](https://jitpack.io/v/appuraja1/AutoScrollViewPager.svg)](https://jitpack.io/#appuraja1/AutoScrollViewPager)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![AndroidX](https://img.shields.io/badge/AndroidX-Compatible-blue.svg)](https://developer.android.com/jetpack/androidx)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A lightweight, modern, and high-performance **Auto-Scrolling ViewPager** for Android. Designed specifically for banner loops, carousel sliders, smooth transitions, and leak-free lifecycle handling on AndroidX.

---

## Features

* 🔄 **Infinite Looping:** Seamless infinite scrolling without flickering or blank frames.
* ⚡ **AndroidX Ready:** Fully migrated to modern Android components and APIs.
* 🛠 **Touch-Friendly:** Pauses automatically on touch and resumes gracefully (`ACTION_DOWN`, `ACTION_UP`, and `ACTION_CANCEL` support).
* 🎯 **Zero Memory Leaks:** Lifecycle-aware handler cleanup tied to window detach events.
* 🎨 **Customizable:** Dynamic slide intervals, animation duration, direction, and programmatic controls.
* 📦 **Efficient View Recycling:** Built-in view recycling mechanism for minimal memory footprint.

---

## Installation

### 1. Add the JitPack repository

Add it to your `settings.gradle` file:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url '[https://jitpack.io](https://jitpack.io)' }
    }
}

Or, if using root build.gradle:Groovyallprojects {
    repositories {
        google()
        mavenCentral()
        maven { url '[https://jitpack.io](https://jitpack.io)' }
    }
}



2. Add the dependencyAdd the library to your module-level build.gradle (usually app/build.gradle):Groovydependencies {
    implementation 'com.github.appuraja1:AutoScrollViewPager:v1.0.1'
}



Quick Start

1. XML LayoutAdd AutoScrollViewPager inside your layout file:XML<com.github.appuraja.AutoScrollViewPager
    android:id="@+id/autoScrollViewPager"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:slideInterval="4000"
    app:slideDuration="800"
    app:slideDirection="right"
    app:stopWhenTouch="true"
    app:cycle="true" />


2. Implement AdapterExtend InfinitePagerAdapter to enable continuous looping and view recycling:Javapublic class BannerAdapter extends InfinitePagerAdapter {

    private final List<String> items;
    private final Context context;

    public BannerAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @NonNull
    @Override
    public View getItemView(int position, @Nullable View convertView, @NonNull ViewGroup container) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load image using Glide or Coil
        Glide.with(context)
                .load(items.get(position))
                .into(imageView);

        return imageView;
    }
}


3. Initialize in Activity or FragmentJavaAutoScrollViewPager viewPager = findViewById(R.id.autoScrollViewPager);

List<String> bannerList = Arrays.asList(
    "[https://example.com/banner1.jpg](https://example.com/banner1.jpg)",
    "[https://example.com/banner2.jpg](https://example.com/banner2.jpg)",
    "[https://example.com/banner3.jpg](https://example.com/banner3.jpg)"
);

BannerAdapter adapter = new BannerAdapter(this, bannerList);
viewPager.setAdapter(adapter);


// Start auto scrolling
viewPager.startAutoScroll();
XML Attributes & API ConfigurationXML AttributeJava MethodDefaultDescriptionapp:slideIntervalsetSlideInterval(int ms)5000Delay between auto transitions in milliseconds.app:slideDurationsetSlideDuration(int ms)800Smooth transition animation time in milliseconds.app:slideDirectionsetDirection(int dir)DIRECTION_RIGHTScroll direction (left or right).app:stopWhenTouchsetStopWhenTouch(boolean)truePauses auto-scroll on user interaction.app:cyclesetCycle(boolean)trueLoop back to initial page when end is reached.Public MethodsstartAutoScroll(): Begins auto-sliding using the configured interval.startAutoScroll(int delayTime): Starts auto-sliding with a custom delay override.stopAutoScroll(): Stops auto-sliding immediately.LicensePlaintextCopyright 2026 Appu Raja





Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
