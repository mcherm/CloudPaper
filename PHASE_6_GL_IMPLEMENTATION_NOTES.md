# Phase 6: GPU-Based Cloud Generation - Implementation Notes

## GLWallpaperService Decision

### Research Summary (December 8, 2025)

After researching GLWallpaperService implementation options, we have two choices:

**Option A: Use GLWallpaperService Library**
- Repository: https://github.com/GLWallpaperService/GLWallpaperService
- License: Apache License 2.0
- Status: Actively used by 60+ apps with 100M+ downloads (as of Sept 2024)
- Requirements: API level 7+ (we're targeting API 24+)
- Distribution: Available via JitPack
- Pros:
  - Well-tested, mature library
  - Handles threading and EGL context management
  - Proven track record with millions of downloads
  - Reduces implementation complexity
- Cons:
  - External dependency
  - Originally designed for older Android (though still works)

**Option B: Implement Custom GLWallpaperService**
- Approach: Adapt GLSurfaceView internals for WallpaperService
- Pros:
  - No external dependencies
  - Full control over implementation
  - Can be tailored to exact needs
- Cons:
  - More complex implementation
  - Need to handle threading, EGL context, lifecycle ourselves
  - Higher risk of bugs
  - Diverts focus from actual cloud rendering

### Decision: Option A - Use GLWallpaperService Library

**Rationale:**
1. Our goal is to implement efficient GPU-based cloud rendering, not to reinvent OpenGL plumbing
2. The library is battle-tested with proven production use
3. It aligns with the project plan's mitigation strategy: "Use well-tested GLWallpaperService library"
4. Reduces implementation time and risk
5. We can always implement a custom version later if needed

### Implementation Details

**Dependency (build.gradle):**
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.markfguerra:GLWallpaperService:1.0'
}
```

**Usage Pattern:**
- Extend `GLWallpaperService` instead of `WallpaperService`
- Implement `GLSurfaceView.Renderer` for our cloud rendering logic
- Handle lifecycle callbacks (onVisibilityChanged, onDestroy)
- Pass renderer to GLEngine

## Resources

### GLWallpaperService Information
- [GitHub - GLWallpaperService/GLWallpaperService](https://github.com/GLWallpaperService/GLWallpaperService)
- [Library Statistics](https://www.appbrain.com/stats/libraries/details/glwallpaperservice/glwallpaperservice)

### OpenGL ES Wallpaper Guides
- [How to Use OpenGL ES 2 in an Android Live Wallpaper](https://www.learnopengles.com/how-to-use-opengl-es-2-in-an-android-live-wallpaper/)
- [OpenGLES2WallpaperService Example](https://github.com/learnopengles/Learn-OpenGLES-Tutorials/blob/master/android/AndroidOpenGLESLessons/app/src/main/java/com/learnopengles/android/livewallpaper/OpenGLES2WallpaperService.java)

## Next Steps

1. Add GLWallpaperService dependency to build.gradle
2. Create GLCloudRenderer.java implementing GLSurfaceView.Renderer
3. Create GLCloudPaperService.java extending GLWallpaperService
4. Implement shader loading and compilation
5. Test on emulator and device
