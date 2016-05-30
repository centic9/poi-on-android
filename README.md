[![Build Status](https://travis-ci.org/centic9/poi-on-android.svg)](https://travis-ci.org/centic9/poi-on-android) [![Gradle Status](https://gradleupdate.appspot.com/centic9/poi-on-android/status.svg?branch=master)](https://gradleupdate.appspot.com/centic9/poi-on-android/status)

This is a sample Android application to show how
Apache POI can be used on Android.

It consists of two projects:
* poishadow: A small helper project to produce
  a shaded jar-file for Apache POI which includes
  all necessary dependencies and fixes a few things
  that usually hinder you deploying Apache POI on
  Android
* poitest: A very small sample Android application
  which performs some actions on an XLSX-file using
  Apache POI. See `DocumentListActivity` for the actual
  code

#### Getting started

    git clone git://github.com/centic9/poi-on-android
    cd poi-on-android
    ./gradlew build connectedCheck

#### Notes

* This was only tested in Android Studio with the Android
  emulator until now, should work on real Android as well, though!
* Tested with `targetSdkVersion 22` and `minSdkVersion 15`,
  although other versions should work as long as they support
  `multiDexEnabled true`

#### Todo
* Add more actual functionality to the sample application,
  currently it just

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

   Copyright 2015-2016 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
