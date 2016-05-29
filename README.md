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

Notes:
* This was only tested in Android Studio with the Android
  emulator until now, should work on real Android as well, though!
* Tested with `targetSdkVersion 22` and `minSdkVersion 15`,
  although other versions should work as long as they support
  `multiDexEnabled true`

Todo:
* Add more actual functionality to the sample application,
  currently it just

Links:
* https://github.com/FasterXML/aalto-xml
* https://github.com/johnrengelman/shadow
* http://www.mysamplecode.com/2011/10/android-read-write-excel-file-using.html
* http://blog.kondratev.pro/2015/08/reading-xlsx-on-android-4-and-hopefully.html
* https://github.com/andruhon/android5xlsx
* http://stackoverflow.com/questions/8493507/trying-to-port-apache-poi-to-android
* http://www.cuelogic.com/blog/creatingreading-an-excel-file-in-android/
* http://en.b-s-b.info/office/excel/java-excel-poi.html
