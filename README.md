LocDoc
======
The project is dependent on two project **File chooser** and **google-play-services_lib**. 

Instructions to build:

1. Clone the repository of **LocDoc** project:
    `git clone https://bitbucket.org/mobilecloudlab/madp_fall2013_researcherjournalapp.git`

2. Import the project into your workspace in Eclipse:
    1. File
    2. Import
    3. General
    4. Existing project into Workspace
    5. Browse to the folder where **LocDoc** is cloned
    6. Finish
3. Download file chooser from here: `https://android-file-chooser.googlecode.com/files/File%20Chooser-src%20%282%29.zip`
    1. If the direct link is not funclioning for some reasons, try getting this like this:
        1. Follow `https://code.google.com/p/android-file-chooser/downloads/list`
        2. Click on `File Chooser-src (2).zip`
        3. Click on `File Chooser-src (2).zip`  
4. Import the project into your workspace in Eclipse:
    1. File
    2. Import
    3. General
    4. Existing project into Workspace
    5. Browse to the folder where **File Chooser** is downloaded
    6. Finish

5. Add the project as a library to your **LocDoc** project:
    1. Right click on your **LocDoc** project
    2. Select 'Properties'
    3. Select 'Android'
    4. Go to 'Library' section, and click 'Add'
    5. Select the imported **File chooser** project
        1. Remove any dead-link project, that are there
    6. Click 'Apply'
    7. Click 'Okay'

6. Enable **Google Play Services**:
    1. In your Eclipse, click 'Window'
    2. Click 'Android SDK MAnager'
    3. Install 'Extras/Google Play Services'

7. Import **google-play-services_lib** into your workspace:
    1. File
    2. Import
    3. Android
    4. Existing Android Code into Workspace
    5. Browse to your `[your-android-sdk]\extras\google\google_play_services\libproject\google-play-services_lib`
        1. To find your Android SDK location, go to **Eclipse Preferences->Android->SDK Location**
    6. Finish

8. Add the project as a library to your LocDoc project:
    1. Right click on your LocDoc project
    2. Select 'Properties'
    3. Select 'Android'
    4. Go to 'Library' section, and click 'Add'
    5. Select the imported **File chooser** project
        1. Remove any dead-link project, that are there
    6. Click 'Apply'
    7. Click 'Okay'

That should remove all the dependencies issue. Should any of the red lines keep bugging you, try cleaning the project (**Project->Clear->OK**)


Google Maps API Key
----------------------

Each machine has its on SHA1 fingerprint, which is used by Google to allow the access to Google Maps API.
As a consequence to that fact, please send us (**b25938 [AT] ut [DOT] ee** or **rrenja [AT] ut [DOT] ee**) your SHA1 finger print, we will accept you and then the application can be used to its full extend.
Instructions for gettig your SHA1 fingerprint:

1. Open **Eclipse preferences**
2. Go to 'Android' -> 'Build'
3. Copy 'SHA1 fingerprint'.
4. Send to any of the team members.
