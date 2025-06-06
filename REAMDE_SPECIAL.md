# Special handling

The following usages require special handling and are only intended for specific cases.

## Purge memory

It's possible to only remove the data that is in memory of Parley. The difference with the `reset()` method is that this one does not update the backend. In fact, this can be seen as the app going 'inactive' and clearing its memory, while the user remains logged in from the backend perspective. However, Parley will not be able to recover from this automatically and therefore it is required to call the `configure()` method again to use Parley.

```kotlin
Parley.purgeLocalMemory()
```

## Lightweight configure

This is a special use case which comes with some trade-offs.

### Setup

To setup Parley without configuring it all, the `setup()` method can be called:

```kotlin
Parley.setup(context, secret)
```

### Register device

To be able to use Parley methods, the device must be registered for the current user:

```kotlin
Parley.registerDevice(object : ParleyCallback {
    override fun onSuccess() {
        onSuccess()
    }

    override fun onFailure(code: Int, message: String) {
        onFailure(code, message)
    }
})
```

