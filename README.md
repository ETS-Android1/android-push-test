# android-push-test

Simple project for Sendbird Push Demo - Includes alwaysReceiveMessage to true

## Configure

Change inside ```MainActivity``` the following data:

```
    String mAppID = "YOUR APPLICATION ID";
    String mUserId = "USER ID HERE";
```


## Always receive message

You can override the following code:

```
    @Override
    protected boolean alwaysReceiveMessage() {
        return true;
    }
```

Returning ```true``` will always send a PUSH notification.
Set to ```false``` for receiving a PUSH only when the application is in the background.

