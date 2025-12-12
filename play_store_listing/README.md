# Play Store Listing Materials

This folder contains all the content needed for the Google Play Store listing.

## Text Content Files

### AppTitle.txt
The app title as it will appear on Google Play (max 50 characters).

### ShortDescription.txt
A brief description shown in search results and lists (max 80 characters).

### FullDescription.txt
The complete app description shown on the app's store page (max 4000 characters).

### Category.txt
The Play Store category for the app.

### PrivacyPolicy.txt
The privacy policy text. This needs to be hosted on a publicly accessible URL before submitting to Play Store.

**Action Required:** Host this privacy policy at a public URL (e.g., GitHub Pages, your website) and provide that URL to Google Play Console.

## Graphics Assets

### graphics/feature_graphic/
Contains the feature graphic (1024 x 500 px) - the banner image shown at the top of your app's Play Store listing.

### graphics/screenshots/
Contains phone screenshots (1080 x 1920 px or similar) showing:
- The wallpaper in action on the home screen
- Settings screen
- Different sky/cloud configurations

**Play Store Requirements:**
- Minimum 2 screenshots required
- Recommended 4-8 screenshots for best presentation
- PNG or JPEG format

### App Icon
The app icon is already configured in the app resources at `app/src/main/res/mipmap-*/ic_launcher.*`

## Next Steps

1. Edit text files as desired
2. Add graphics to the appropriate directories
3. Host the privacy policy at a public URL
4. Upload everything to Google Play Console
