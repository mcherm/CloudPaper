# Android Live Wallpaper Project Plan
## CloudPaper - Sky with Moving Clouds

## Project Overview
Create an Android live wallpaper application that displays an animated sky background with moving clouds. This will serve as both a functional live wallpaper and a foundation for understanding Android live wallpaper development.

## Technical Requirements

### Platform Requirements
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34) or latest
- **Language**: Java
- **Build System**: Gradle with Android Gradle Plugin (required for Android development)

### Core Dependencies
- Android SDK
- AndroidX libraries
- Canvas/View rendering APIs

## Architecture

### Key Components

#### 1. WallpaperService
- Extend `android.service.wallpaper.WallpaperService`
- Implement `Engine` inner class to handle rendering lifecycle
- Manage surface callbacks (created, changed, destroyed)

#### 2. Rendering Engine
- **Canvas-based rendering**
  - Use `Canvas` API for drawing
  - Paint objects for sky gradient and clouds
  - Supports pixel-level manipulation via Bitmap for noise-generated clouds
  - Bitmap.setPixels() allows efficient procedural cloud generation
  - Paint filters (BlurMaskFilter) provide soft, blurry cloud edges

#### 3. Animation System
- Custom animation loop using Handler
- **Very slow update rate: ~2 FPS** (500ms between updates)
  - Creates a nearly static background that changes slowly over time
  - Minimizes resource consumption and battery drain
  - Still provides gentle cloud movement
- Frame rate may be adjusted during development to find optimal feel
- Update cloud position and structure on each frame tick

#### 4. Cloud System
- Algorithm for generating cloud images
- Rendering that noise to create a single static frame
- Adjusting a parameter to advance time to create subsequent frames
- Adjusting the offset of the generation to create "drift"

## Development Phases

**Testing Philosophy**: Each phase includes deployment to an actual device for testing. This iterative approach ensures the build process works correctly and allows for confident, incremental development. Test on both emulator and real device throughout.

### Phase 1: Project Setup
- [X] Create new Android project in Android Studio
- [X] Configure build.gradle files
- [X] Set up project structure
- [X] Add necessary permissions to AndroidManifest.xml

### Phase 2: Basic Wallpaper Service
- [X] Create WallpaperService class
- [X] Implement Engine inner class
- [X] Handle surface lifecycle callbacks (onCreate, onSurfaceCreated, onSurfaceChanged, onSurfaceDestroyed, onVisibilityChanged)
- [X] Draw simple solid color background
- [X] Build and deploy to actual device
- [X] Test wallpaper installation and verify it displays on device (even if just solid color)

### Phase 3: Noise-Based Cloud Generation
- [X] Select noise generation library (selected FastNoiseLite)
- [X] Use OpenSimplex2S noise. Use 3D noise (X,Y will render the screen; Z will be time). Use 3 octaves of FBm Fractal.
- [X] Render noise-generated clouds over sky gradient
- [X] Use a threshhold to provide "clear sky" background.
- [X] Deploy to device and test static cloud display on both emulator and actual device

### Phase 4: Procedural Animation (Evolution Over Time)
- [X] Implement animation loop with Handler. Create an adjustable FPS (default to 2 FPS)
- [X] Add time parameter that increments each frame. Base it on time passed, not frames rendered
- [X] Use time parameter to provide the z-position
- [X] Deploy to device and test animation on both emulator and actual device

### Phase 5: Procedural Animation (Drift)
- [X] Create a parameter for rate and direction of "drift" or "wind"
- [X] Default to a fairly low rate of drift in an east-south-east direction
- [X] Use the drift parameter to offset the x and y coordinates used in generateClouds()
- [X] Deploy to device and test animation on both emulator and actual device

### Phase 6: Polish & Settings (Optional)
- [X] Create settings activity
- [X] Implement SharedPreferences integration
- [X] Add user preferences for each of the adjustable parameters, including
  - Animation FPS (frame rate adjustment)
  - Evolution time (how fast the Z parameter changes)
  - Drift speed (how fast clouds move)
  - Cloud density/intensity (noise threshold adjustments)
- [X] Create "credits" activity to show copyright notices and credits
- [X] Deploy and test settings on device

### Phase 7: Code Cleanup
- [X] Check for warnings and resolve
- [X] Change package from com.example.cloudpaper to com.mcherm.cloudpaper
- [X] Upgrade target Java version from 8 to something more modern
- [X] Address all "FIXME" comments

### Phase 8: Testing & Deployment
- [X] Test on multiple devices/screen sizes
- [X] Test battery impact and performance
- [X] Create app icon and preview image
- [ ] Prepare for Play Store (if desired)
- [ ] Generate signed APK/AAB

## Project Structure

```
app/
├── src/main/
│   ├── java/com/mcherm/cloudpaper/
│   │   ├── CloudPaperService.java     # Main WallpaperService
│   │   ├── CloudEngine.java           # Engine inner class (handles lifecycle)
│   │   ├── noise/
│   │   │   └── NoiseGenerator.java    # Perlin/Simplex noise implementation
│   │   ├── renderer/
│   │   │   ├── SkyRenderer.java       # Sky gradient rendering
│   │   │   └── CloudRenderer.java     # Procedural cloud generation & rendering
│   │   └── settings/
│   │       └── SettingsActivity.java  # User preferences (optional)
│   ├── res/
│   │   ├── layout/
│   │   │   └── settings_activity.xml
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   └── colors.xml
│   │   └── xml/
│   │       └── wallpaper.xml          # Wallpaper metadata
│   └── AndroidManifest.xml
```

## Key Implementation Details

### AndroidManifest.xml Configuration
```xml
<service
    android:name=".CloudPaperService"
    android:permission="android.permission.BIND_WALLPAPER"
    android:exported="true">
    <intent-filter>
        <action android:name="android.service.wallpaper.WallpaperService" />
    </intent-filter>
    <meta-data
        android:name="android.service.wallpaper"
        android:resource="@xml/wallpaper" />
</service>
```

### Animation Loop Pattern
```java
private Handler handler = new Handler(Looper.getMainLooper());
private Runnable drawRunnable = new Runnable() {
    @Override
    public void run() {
        draw();
        if (visible) {
            handler.postDelayed(this, 500); // ~2 FPS (adjust as needed)
        }
    }
};
```

### Procedural Generation Parameters
- **Time parameter**: Advances each frame to animate cloud evolution
  - Increment time value each update (e.g., `time += 0.01`)
  - Feed into noise function to create organic cloud changes
- **Offset/Drift parameters**: Create horizontal cloud movement
  - Increment offset each frame (e.g., `xOffset += 0.5`)
  - Shifts the noise sampling coordinates to simulate wind/drift
- **Noise function**: Use Perlin or Simplex noise for natural cloud shapes
  - Takes (x + xOffset, y, time) as input
  - Returns values for cloud density/opacity at each pixel

## Performance Considerations

### Optimization Strategies
1. **Ultra-low frame rate**: ~2 FPS (500ms updates) makes procedural generation viable
   - Only regenerates full sky texture twice per second
   - Drastically reduces CPU usage compared to typical 30-60 FPS wallpapers
2. **Efficient noise implementation**: Use optimized Perlin/Simplex noise algorithm
   - Consider using existing libraries (e.g., SimplexNoise) rather than implementing from scratch
   - Optimize noise calculations for mobile performance
3. **Reuse Bitmap and pixel array**: Allocate once, rewrite pixels each frame
   - Create single mutable Bitmap at startup
   - Reuse int[] pixel array for Bitmap.setPixels()
   - Avoid allocations in the draw loop
4. **Reduce allocations**: Reuse Paint objects and other resources
5. **Visibility checks**: Only generate/animate when wallpaper is visible
6. **Resolution optimization**: Consider generating at slightly lower resolution if needed for performance

### Battery Impact
- **Minimal impact due to 2 FPS update rate** - CPU wakes only twice per second
- Pause animation when screen is off using `onVisibilityChanged()` callback
- Procedural generation at 2 FPS is computationally light on modern devices
- Should have low battery drain compared to typical live wallpapers (30-60 FPS)

## Testing Strategy

### Manual Testing
- Install as live wallpaper on device
- Test on different screen sizes and orientations
- Monitor battery drain over time
- Check performance (frame rate, smoothness)
- Test settings changes (if implemented)

### Edge Cases
- Screen rotation
- Multi-window mode
- Low memory conditions
- Different Android versions

## Future Enhancements (Optional)
- [ ] Day/night cycle
- [ ] Weather effects (rain, storms)
- [ ] Interactive touch response
- [ ] Seasonal variations
- [ ] Multiple sky themes
- [ ] 3D clouds using OpenGL

## Resources & References

### Official Documentation
- [Android Live Wallpaper Guide](https://developer.android.com/develop/ui/views/launch/wallpaper)
- [WallpaperService API](https://developer.android.com/reference/android/service/wallpaper/WallpaperService)
- [Canvas and Drawables](https://developer.android.com/develop/ui/views/graphics)

### Learning Resources
- Android Developer Codelabs
- Sample live wallpaper projects on GitHub
- Android Canvas drawing tutorials

## Success Criteria
- ✓ Wallpaper successfully installs and displays on Android device
- ✓ Clouds animate smoothly across the screen
- ✓ No significant battery drain
- ✓ Works across different screen sizes
- ✓ Clean, maintainable code structure

## Timeline Estimate
This is a beginner-to-intermediate project. Core functionality (Phases 1-5) represents the essential work needed for a functional live wallpaper with cloud animation.

## Notes
- Using Canvas-based rendering for pixel-level control and simplicity
- Target aesthetic: nearly static background with very slow, subtle cloud movement
- Animation frame rate (~2 FPS) is intentionally slow - adjust during testing to find right feel
- Procedurally generate entire sky each frame using noise functions (Perlin/Simplex)
- Time and offset parameters create organic evolution and drift
- Focus on getting basic procedural generation and animation working before adding polish
- Test frequently on actual device (emulator may not accurately show performance)
- Monitor performance during testing; adjust noise complexity or resolution if needed
