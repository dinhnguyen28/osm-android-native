## Description
A simple mobile application – working title: “Landmark Remark”
- that allows users to save location based notes on a map. These notes can be
displayed on the map where they were saved and viewed by the user that
created the note as well as other users of the application.

## Features
Main features of app:
1. Fake login
2. Show current my location
3. Click on the map to mark the location and save this marker
4. Click the marker to display marker information
4. Open Street Map library
5. Search notes by username
6. Click on a note to display the note's location on the map

## Technology Stack Used
1. Android Native (Kotlin)
3. Firestore Database (for data persistence)
4. Open Street Map library (to display all marker)
5. BottomSheetDialogFragment (for displaying marker info)
6. Dagger Hilt (Injection)

## Bugs
1. Showing current location doesn't work as expected, has to be pressed twice.

## Testing
For testing purposes, you can follow these steps

username: *Enter any username you want*

1. Map (MapFragment) - Displays all the markers of all users (location color red for my own marker, blue flag for other users markers)
2 .My Notes (MyNotesFragment) - Displays only Notes of the current user



| ![login_screen](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/419d34c3-02c1-4297-9ff6-d90ceafac8d1) | ![Map](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/90f1e4a4-ab95-46b2-aa03-96b134623c3b) | ![marker_info](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/006d182c-786e-40da-831d-4fd5bcda5f97) | ![other_marker_info](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/d1101529-b463-45ed-aaa2-62ada9b0403d) |
|:---:|:---:|:---:|:---:|
| ![my_notes](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/0a253fb1-72d1-4c8e-971c-a519f8389a46) | ![search_note](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/1cf2ffca-6b89-4308-a373-dbaa2e1f1b8a) | ![pin_location](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/e8bce10c-a98f-42cd-8080-123a21da92b3) | ![my_current_location](https://github.com/dinhnguyen28/osm-android-native/assets/82631708/9eaf6b42-c991-4963-81ec-3f3295721129) |
