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

username: *<Enter any username you want>*

1. Map (MapFragment) - Displays all the markers of all users (location color red for my own marker, blue flag for other users markers)
2 .My Notes (MyNotesFragment) - Displays only Notes of the current user



| ![Login]() | ![HomeScreen]()| ![MarkerSheet]() | ![Map]() |
|:---:|:---:|:---:|:---:|
| ![MyNotes]()| ![Share]() | ![Profile]() | ![Favorites]() |
