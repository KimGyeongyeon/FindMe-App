# FindMe-App

## Summary
> FindMe is an application for users who want to find lost pets.
+ Frontend: Android studio, Java
+ Backend: Firebase (Firebase Authentication, Firebase Cloud Messaging, Firebase Firestore, Firebase Storage)
+ API / Library: Google Map, Glide

--------------------------------------------

## Login / Register

> Backend
  + Firebase Authentication
  + Need a Gmail Account

> Implementation
  + LoginActivity2.java
  + RegisterActivity.java

--------------------------------------------

## Main page

> Show all lost pets on the map with a photo marker

> Implementation
  + MainActivity.java

--------------------------------------------

## Pet Detail

> Can see detailed information about the lost pet.  
> Can click Not Here button to report that a lost pet is not around you.  

> Implementation
  + PetInfoActivity.java
  + PetInfoMapActivity.java
  + ShowHereActivity.java
  + NotHere class
  + HereReportCard.java

--------------------------------------------

## Post

> Can take a picture of a lost pet a user found and upload the post.  
> Can link the pet with existing lost pet in the App.  
> Can post a pet that seems lost but do not exist in the App.  

> Implementation
  + PostActivity.java
  + missingAdapter.java
  + missingContainer.java

--------------------------------------------

## Game

> Can contribute to finding lost pet with a fun game even if the user is not near the lost pet.  
> User is asked to compare the two images and click check if they are same pet, and x if not.

> Implementation
  + GameActivity.java
  + GameIntroActivity.java

--------------------------------------------

## Notification

> Send notification the users to motivate them to find lost pets.
> Used Firebase Cloud Messaging

> Implementation
  + MainActivity.java
  + FireBaseMessagingService.java

--------------------------------------------

## Hard Coded Parts

> Missing pet reports are already uploaded in the App.  
> Game data randomize not implemented.  
> Notification message is send to all users using the app at the same time.  
