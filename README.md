# KeepLivePro

### 一.Outline

Android keep live project, Show the way to keep android project live.

The current demo show the three ways to keep.
1. Start one pixel activity throught service.
2. Start forground service.
3. Wake up each other.

### 二.Remark
1. adb root to get root permission.
2. adb shell ps(for processes current);adb shell ps | grep com.astana.cpy.keeplive(for check proecess of app)
3. adb shell cat /proc/{process id of app}/oom_adj(for check the process priority)
4. adb shell kill service {process-id}(for kill process with specified id )
