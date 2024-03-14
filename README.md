[![Build Status](https://github.com/centic9/poi-on-android/actions/workflows/build.yaml/badge.svg)](https://github.com/centic9/poi-on-android/actions)
[![Gradle Status](https://gradleupdate.appspot.com/centic9/poi-on-android/status.svg?branch=master)](https://gradleupdate.appspot.com/centic9/poi-on-android/status)
[![Release](https://img.shields.io/github/release/centic9/poi-on-android.svg)](https://github.com/centic9/poi-on-android/releases)
[![GitHub release](https://img.shields.io/github/release/centic9/poi-on-android.svg?label=changelog)](https://github.com/centic9/poi-on-android/releases/latest)

This is a sample Android application to show how
Apache POI can be used on Android.

It consists of two projects:
* __poishadow__: A small helper project to produce
  a shaded jar-file for Apache POI which includes
  all necessary dependencies and fixes a few things
  that usually hinder you deploying Apache POI on
  Android
* __poitest__: A very small sample Android application
  which performs some actions on XLSX-, PPTX and DOCX-files using
  Apache POI. See `MainActivity` for the actual
  code

#### Getting started

##### Necessary System-Properties

In order to work around problems with finding a suitable XML Parser, currently
the following system properties need to be set manually during startup of your
application (let me know if you know of a better way to do this, see issue #10)

    System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
    System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
    System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

##### Supported Android versions

The sample project uses minimum SDK version 26, which maps to Android 8.0. Higher versions should work as well, 
older ones are likely not supported any more.

##### Dependencies

Note: Some dependencies of Apache POI are not included in the shading to keep it's size at bay. If you use code
areas which require `curvesapi`, `commons-codec` or any of the other dependencies, you may need to add
them in your Android application in addition to the poishadow-all.jar file dependency.

##### Missing java.awt classes

Unfortunately Android does not provide any of the classes in package `java.awt` and 
thus any code which uses code from there will not work. This affects various places
in Apache POI, e.g. when handling column-width, images or other graphical operations.

A few classes have been re-implemented in the src-folder in project `poishadow`. If you
are missing some you might be able to add re-implementations for more there.

We do not plan to fix all of these in this sample application, if you need more, feel
free to add it and send PRs if you think it is generally useful.

##### Use a pre-built jar

If you want to get started quickly, there is a ready-made jar-file available in the 
[release section](https://github.com/centic9/poi-on-android/releases), however this is
not updated frequently, so it may be somewhat outdated at times. 

You should be able to simply add this to your Android project and use the Apache POI
classes from it.

##### Build the jar yourself

If you would like to get the most recent jar or if you would like to change how the 
jar-file is built,  e.g. if you need classes that
are excluded, use a different version of POI or would like to adjust the build in some
other way, you can build the shaded jar with the following steps:

Preparation:

You will need the following pieces in order to get started

* A recent Java SDK, preferably Java 8
* An installation of the Android SDK, either the one included
  with Android Studio or a separate download, see
  https://developer.android.com/studio/index.html#downloads

Get the code:

    git clone https://github.com/centic9/poi-on-android.git
    cd poi-on-android

Configure where your Android SDK resides:

    echo "sdk.dir=/opt/android-sdk-linux" > local.properties

Configure the version of the Android Build Tools that you have installed.

    vi poitest/build.gradle

Then build the shadow-jar (for some reason this works better if executed separately):

    ./gradlew shadowJar

Finally run the build and some testing. Make sure you have a device connected, e.g. the Android emulator.

    ./gradlew build connectedCheck

For only the jar-files just run `build`

#### Support this project

If you find this tool useful and would like to support it, you can [Sponsor the author](https://github.com/sponsors/centic9)

#### Run the Android emulator

List available emulators

    <android-sdk>/tools/emulator -list-avds

Start an Android emulator

    <android-sdk>/tools/emulator -avd <name>

Install the apk

    <android-sdk>/platform-tools/adb install ./poitest/build/outputs/apk/poitest-debug.apk

#### Notes

* You can use the resulting jar-file `poishadow/build/libs/poishadow-all.jar`
  in your own project, the code in directory `poitest` is
  just a small sample Android application to show that it works.
* This was only tested in Android Studio with the Android
  emulator until now, should work on real Android as well, though!
* Tested with `targetSdkVersion 26` and `minSdkVersion 26`

#### Todo

* Add more actual functionality to the sample application,
  currently it just performs some basic functionality on
  documents.
* Some parts of the JDK are missing on Android, e.g. AWT-related classes.
  Some of these are missing when some functionality of Apache POI
  is used and thus currently crash the sample project. 
  There are some libraries which provide these classes, but none
  was in a state that allowed it to easily integrate it here.

PR which improve on these items are welcome.

#### Links

* https://github.com/FasterXML/aalto-xml
* https://github.com/johnrengelman/shadow
* http://www.mysamplecode.com/2011/10/android-read-write-excel-file-using.html
* http://blog.kondratev.pro/2015/08/reading-xlsx-on-android-4-and-hopefully.html
* https://github.com/andruhon/android5xlsx
* http://stackoverflow.com/questions/8493507/trying-to-port-apache-poi-to-android
* http://www.cuelogic.com/blog/creatingreading-an-excel-file-in-android/
* http://en.b-s-b.info/office/excel/java-excel-poi.html

#### Licensing

   Copyright 2015-2024 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
