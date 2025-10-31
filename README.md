# capacitor-android-navbar-info

Capacitor plugin for Android providing information about the navigation bar, like navigation bar height, gesture navigation mode, and more

## Install

```bash
npm install @chefslist/capacitor-android-navigation-bar-info
npx cap sync
```

## Usage

### Get Navigation Bar Info

```typescript
import { NavigationBarInfo } from '@chefslist/capacitor-android-navigation-bar-info';

const info = await NavigationBarInfo.getNavigationBarInfo();
console.log('Navigation bar height:', info.heightInDp);
console.log('Device height:', info.deviceHeight);
console.log('Is visible:', info.isNavigationBarVisible);
console.log('Is gesture navigation:', info.isGestureNavigation);
```

### Listen for Navigation Bar Changes

The plugin automatically detects when the navigation bar info changes (e.g., when switching between windowed and fullscreen modes on Android 15+):

```typescript
import { NavigationBarInfo } from '@chefslist/capacitor-android-navigation-bar-info';

// Add listener for navigation bar changes
await NavigationBarInfo.addListener('navigationBarInfoChanged', (info) => {
  console.log('Navigation bar info changed:', info);
  // Update your UI based on new navigation bar dimensions
});

// Don't forget to remove listeners when component unmounts
await NavigationBarInfo.removeAllListeners();
```

## API

<docgen-index>

* [`getNavigationBarInfo()`](#getnavigationbarinfo)
* [`addListener('navigationBarInfoChanged', ...)`](#addlistenernavigationbarinfochanged-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getNavigationBarInfo()

```typescript
getNavigationBarInfo() => Promise<NavigationBarInfo>
```

**Returns:** <code>Promise&lt;<a href="#navigationbarinfo">NavigationBarInfo</a>&gt;</code>

--------------------


### addListener('navigationBarInfoChanged', ...)

```typescript
addListener(eventName: 'navigationBarInfoChanged', listenerFunc: (info: NavigationBarInfo) => void) => Promise<PluginListenerHandle>
```

Listen for navigation bar info changes (e.g., when switching between windowed and fullscreen modes)

| Param              | Type                                                                               |
| ------------------ | ---------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'navigationBarInfoChanged'</code>                                            |
| **`listenerFunc`** | <code>(info: <a href="#navigationbarinfo">NavigationBarInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all listeners for this plugin

--------------------


### Interfaces


#### NavigationBarInfo

| Prop                         | Type                 |
| ---------------------------- | -------------------- |
| **`heightInDp`**             | <code>number</code>  |
| **`deviceHeight`**           | <code>number</code>  |
| **`density`**                | <code>number</code>  |
| **`isNavigationBarVisible`** | <code>boolean</code> |
| **`isGestureNavigation`**    | <code>boolean</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
