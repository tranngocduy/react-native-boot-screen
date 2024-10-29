
# react-native-boot-screen

Display a splash screen on your app starts. Hide it when you want.



### Installation

```
$ npm install --save react-native-bootscreen
# --- or ---
$ yarn add react-native-bootscreen
```

### iOS
Edit the `ios/YourApp/AppDelegate.mm` file:

```obj-c
#import "AppDelegate.h"
#import "RNBootSplash.h" // ⬅️ add the header import

// …

@implementation AppDelegate

// …

// ⬇️ Add this before file @end (for react-native 0.74+)
- (void)customizeRootView:(RCTRootView *)rootView {
  [super customizeRootView:rootView];
  [RNBootSplash initWithStoryboard:@"BootSplash" rootView:rootView]; // ⬅️ initialize the splash screen
}

// OR

// ⬇️ Add this before file @end (for react-native < 0.74)
- (UIView *)createRootViewWithBridge:(RCTBridge *)bridge
                          moduleName:(NSString *)moduleName
                           initProps:(NSDictionary *)initProps {
  UIView *rootView = [super createRootViewWithBridge:bridge moduleName:moduleName initProps:initProps];
  [RNBootSplash initWithStoryboard:@"BootSplash" rootView:rootView]; // ⬅️ initialize the splash screen
  return rootView;
}

@end
```
### Android

Add this line at bottom your `android/app/src/main/res/values/styles.xml` file:
```
<resources>

    ...

    <style name="BootTheme" parent="Theme.BootSplash.TransparentStatus">
        <item name="darkContentBarsStyle">true</item>
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:background">@drawable/launch_screen</item>
    </style>

</resources>
```

Edit your `android/app/src/main/java/com/yourapp/MainActivity.{java,kt}` file:

```java
// Java (react-native < 0.73)
// …

// add these required imports:
import android.os.Bundle;
import com.zoontek.rnbootsplash.RNBootSplash;

public class MainActivity extends ReactActivity {

  // …

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    RNBootSplash.init(this, R.style.BootTheme); // ⬅️ initialize the splash screen
    super.onCreate(savedInstanceState); // super.onCreate(null) with react-native-screens
  }
}
```

```kotlin
// Kotlin (react-native >= 0.73)
// …

// add these required imports:
import android.os.Bundle
import com.zoontek.rnbootsplash.RNBootSplash

class MainActivity : ReactActivity() {

  // …

  override fun onCreate(savedInstanceState: Bundle?) {
    RNBootSplash.init(this, R.style.BootTheme) // ⬅️ initialize the splash screen
    super.onCreate(savedInstanceState) // super.onCreate(null) with react-native-screens
  }
}
```
### API

### hide()

Hide the splash screen (immediately, or with a fade out).

#### Method type

```ts
type hide = (config?: { fade?: boolean }) => Promise<void>;
```

#### Usage

```tsx
import { useEffect } from "react";
import { Text } from "react-native";
import BootSplash from "react-native-bootsplash";

const App = () => {
  useEffect(() => {
    const init = async () => {
      // …do multiple sync or async tasks
    };

    init().finally(async () => {
      await BootSplash.hide({ fade: true });
      console.log("BootSplash has been hidden successfully");
    });
  }, []);

  return <Text>My awesome app</Text>;
};
```

### isVisible()

Return the current visibility status of the native splash screen.

#### Method type

```ts
type isVisible = () => Promise<boolean>;
```

#### Usage

```ts
import BootSplash from "react-native-bootsplash";

BootSplash.isVisible().then((value) => console.log(value));
```

### useHideAnimation()

A hook to easily create a custom hide animation by animating all splash screen elements using `Animated`, `react-native-reanimated` or else (similar as the video on top of this documentation).

#### Method type

```ts
type useHideAnimation = (config: {
  ready?: boolean; // a boolean flag to delay the animate execution (default: true)

  // the required generated assets
  manifest: Manifest; // the manifest file is generated in your assets directory
  logo?: ImageRequireSource;
  darkLogo?: ImageRequireSource;
  brand?: ImageRequireSource;
  darkBrand?: ImageRequireSource;

  // specify if you are using translucent status / navigation bars
  // in order to avoid a shift between the native and JS splash screen
  statusBarTranslucent?: boolean;
  navigationBarTranslucent?: boolean;

  animate: () => void;
}) => {
  container: ContainerProps;
  logo: LogoProps;
  brand: BrandProps;
};
```

#### Usage

```tsx
import { useState } from "react";
import { Animated, Image } from "react-native";
import BootSplash from "react-native-bootsplash";

type Props = {
  onAnimationEnd: () => void;
};

const AnimatedBootSplash = ({ onAnimationEnd }: Props) => {
  const [opacity] = useState(() => new Animated.Value(1));

  const { container, logo /*, brand */ } = BootSplash.useHideAnimation({
    manifest: require("../assets/bootsplash/manifest.json"),

    logo: require("../assets/bootsplash/logo.png"),
    // darkLogo: require("../assets/bootsplash/dark-logo.png"),
    // brand: require("../assets/bootsplash/brand.png"),
    // darkBrand: require("../assets/bootsplash/dark-brand.png"),

    statusBarTranslucent: true,
    navigationBarTranslucent: false,

    animate: () => {
      // Perform animations and call onAnimationEnd
      Animated.timing(opacity, {
        useNativeDriver: true,
        toValue: 0,
        duration: 500,
      }).start(() => {
        onAnimationEnd();
      });
    },
  });

  return (
    <Animated.View {...container} style={[container.style, { opacity }]}>
      <Image {...logo} />
      {/* <Image {...brand} /> */}
    </Animated.View>
  );
};

const App = () => {
  const [visible, setVisible] = useState(true);

  return (
    <View style={{ flex: 1 }}>
      {/* content */}

      {visible && (
        <AnimatedBootSplash
          onAnimationEnd={() => {
            setVisible(false);
          }}
        />
      )}
    </View>
  );
};
```
