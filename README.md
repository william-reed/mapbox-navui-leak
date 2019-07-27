# mapbox navigation ui memory leak & view model retention issue

`'com.mapbox.mapboxsdk:mapbox-android-navigation-ui:0.41.0'` currently has a memory leak issue stemming from the `NavigationView.java` implementation. This view creates a `NavigationPresenter` which retains a reference to the view, and then this presenter is also passed along to the `NavigationViewSubscriber` which retains it for _way_ too long. 

This repo serves as a demo for that. Leak canary used in my production app demonstrates precisely but the issue still occurs here. Without leak canary to help, the issue can be observed by placing breakpoints in `NavigationViewSubscriber` inside of the `onChanged` method of `navigationViewModel.route.observe` around line `25`. This will help exemplify the issues shown below. 

## `NavigationView` & lifecycle issue
Navigating to the `subscribeViewModels()` method on line `694` of the `NavigationView` file we can see the creation of a `NavigationViewSubscriber` in which a `LifecycleOwner` and `NavigationViewModel` is passed into. The lifecycle owner is set to `getContext()` (??????!?!??!) so it will live for the duration of the entire application. 

I'm not positive what should be here but I am certain that is not what is desired. Since it's created in a view, the most likely solution is just a custom implementation based on the view lifecycle. 

## Memory Leak
With these `Observer`'s (in `NavigationViewSubscriber`) getting tied to the application context, they won't be destroyed for the life of the app, and since they contain a reference to the `NavigationPresenter` which in turn contains a reference to the `NavigationView` they are leaking the view.

## Recurrently
Since the `Observer` is never killed, and it is created every time we create the `NavigationView`, we wind up leaking a lot of different `NavigationView`'s if the user goes back and forth in and out of the activity / fragment with that view so the effects are compounded.

## Solutions
1. For the `Observer` issue as mentioned above, the solution is likely creating a custom `LifecycleOwner` for it to use, passing in the context is _not_ suitable for use here
2. For the memory leak a weak reference is probably a good way to just avoid this situation in the presenter

I would have just taken a shot at implementing these instead but I can't seem to find any documentation for building from source (as the main `README.md` suggests existence of).
