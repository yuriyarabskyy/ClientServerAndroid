## Quality assurance, debugging tools, bug tracking/resolving procedures

Testing is an essential part of every development process. It makes sure that the product is reliable and that the customer is satisfied with its quality and functionality. But let's be honest - nobody likes writing and running tests. This process takes up a lot of energy and time, both of which could be invested in some more important things, like developing new features for our product. But on the other hand these features could be buggy and nobody likes to play with buggy programs. Therefore I've taken it to myself to make the process of testing and debugging as painless and automatic as possible.

First of all I suggest using continuous integration in our development process. It's a development practice that requires developers to integrate code into a shared repository several times a day. Each check-in is then verified by an automated build, allowing teams to detect problems early. For this purpose I've chosen Jenkins as our continuous integration server. There are two most popular choices: Travis and Jenkins. Even though Travis is a bit easier to set up (just by adding travis.yml) into our repository, it requires permanent connection to the internet, because it cannot be hosted on a local computer. Jenkins on the other hand is hostable on every computer. I've already installed, set it up and tested with and android project on my computer and it works flawlessly.

We are going to have a connection to a local git repository and I could connect my continuous integration server to it and every time somebody makes a push request, an automatic build is triggered and if it was successful, the artifacts (apk files in this situation) are generated and reports on the tests are published and available for everyone to download.

And let's get to the most important part, which is writing tests. Android Studio has made it for us quite simple. With just a few clicks, we can set up a JUnit test that runs on a local JVM or an instrumented test that runs on a device. We can also use some testing frameworks, such as Mockito (for Android API calls) and Espresso for user interface testing.

### Local JUnit tests

* Located at module-name/src/test/java/
* They do not have access to functional Android framework APIs

In your app's top-level build.gradle file, you need to specify these libraries as dependencies:

```gradle
dependencies {
    // Required -- JUnit 4 framework
    testCompile 'junit:junit:4.12'
    // Optional -- Mockito framework
    testCompile 'org.mockito:mockito-core:1.10.19'
    // Optional -- Hamcrest
    testCompile 'org.hamcrest:hamcrest-library:1.3'
}
```

To write a test we just need to prefix our test method name with the keyword "Test".  "assertEquals" the most important assertion you need to know.

```java
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
```

If you want to make your code slightly more readable you can use Hamcrest. It uses assertThat method with a matcher expression to determine if the test was succesful.

```java
// JUnit 4 for equals check
assertEquals(expected, actual);
// Hamcrest for equals check
assertThat(actual, is(equalTo(expected)));
// JUnit 4 for not equals check
assertFalse(expected.equals(actual));
// Hamcrest for not equals check
assertThat(actual, is(not(equalTo(expected))));
```

Important hamcrest matchers are going to be listed on my slides.

### Android tests

* Located at module-name/src/androidTest/java/
* They run on an android device or an emulator

Instrumented tests are built into an APK that runs on the device alongside your app under test. The system runs your test APK and your app under tests in the same process, so your tests can invoke methods and modify fields in the app, and automate user interaction with your app.

For these tests we need the Android Testing Support Library, which includes the AndroidJUnitRunner and APIs for functional UI tests (Espresso).

Code to add into build.gradle:

```gradle
dependencies {
    androidTestCompile 'com.android.support:support-annotations:24.0.0'
    androidTestCompile 'com.android.support.test:runner:0.5'
    androidTestCompile 'com.android.support.test:rules:0.5'
    // Optional -- Hamcrest library
    androidTestCompile 'org.hamcrest:hamcrest-library:1.3'
    // Optional -- UI testing with Espresso
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
      exclude group: 'com.android.support', module: 'support-annotations'
    })
    // Optional -- UI testing with UI Automator
    androidTestCompile 'com.android.support.test.uiautomator:uiautomator-v18:2.1.2'
}
android {
    defaultConfig {
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
}
```

Tests can be grouped into suites and run together.

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationTest.class,
LogHistoryAndroidUnitTest.class})
public class UnitTestSuite {
}
```

### Testing the UI

For UI testing we will be using Espresso. Gradle setup:

```gradle
dependencies {
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.1'
}
```

Workflow: first we find the view, perform an action, repeat the process and then check the result on the target view.

```java
onView(withId(R.id.my_view))            // withId(R.id.my_view) is a ViewMatcher
        .perform(click())               // click() is a ViewAction
        .check(matches(isDisplayed())); // matches(isDisplayed()) is a ViewAssertion
```

### Debugging tools

Android Studio comes loaded with debugging tools.

* The **ADB** (Android Debug Bridge) is an application that allows you to communicate with a connected emulator or physical Android device.
* **DDMS** (Dalvik Debug Monitor Server) is a debugging tool, which provides the following: logcat, thread and heap information, screen capture etc.

**How does ADB work?** It is comprised of a client app that runs on the development machine, a server that runs on a background process on the development machine, and a daemon that runs on the emulator or device.

**Methods to debug your app:**

* You can print out log statements from your app using logcat. It's a useful utility, which prints out different system events. All events have some priority: errors have the highest one and vebose - the lowest. You can use different methods of the Log class to assign a priority of your log.

```java
private static final String TAG = "MyActivity";
...
Log.e(TAG, "Could not instantiate some object");
```

* When you are at the point where you can’t fix a bug just by looking at your code, it’s time to use breakpoints. Breakpoints allow you to pause the execution of your app at a particular line of code.

