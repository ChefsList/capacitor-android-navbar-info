# system-bars

Capacitor plugin for Android providing information about the navigation bar, like navigation bar height, gesture navigation mode, and more

## Install

```bash
npm install @chefslist/capacitor-android-navigation-bar-info
npx cap sync
```

## API

<docgen-index>

* [`getNavigationBarInfo()`](#getnavigationbarinfo)
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


### Interfaces


#### NavigationBarInfo

| Prop                         | Type                 |
| ---------------------------- | -------------------- |
| **`heightInDp`**             | <code>number</code>  |
| **`deviceHeight`**           | <code>number</code>  |
| **`density`**                | <code>number</code>  |
| **`isNavigationBarVisible`** | <code>boolean</code> |
| **`isGestureNavigation`**    | <code>boolean</code> |

</docgen-api>
