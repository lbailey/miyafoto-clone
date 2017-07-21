# miyafoto-clone

A light-weight image management system that interfaces to Flickr for managing raw image files in a year-to-album organized environment.  

View an example application at [Miyafoto-Clone](http://miyafoto-clone.herokuapp.com/). user/pass: rick/schwifty, morty/szechuan.

## Future TODO

- admin control screen for managing, deleting albums
- admin control screen for adding users, updating passwords

## Setup

- create and setup new Flickr account (do not use existing account)
- upload empty image (Flickr requires an album to contain at least one image) with [this image](https://c1.staticflickr.com/3/2811/33918511672_8e4c3188f4_o.gif)
- update EMPTY_PHOTO value, PROJECT_NAME value in /src/main/java/../Integration.java line 65, 66 following [this guide](http://i.imgur.com/JiRc5Ty.png)
- follow interactions to get API key and Shared Secret with [these Flickr instructions](http://www.flickr.com/services/apps/create/apply/)
- update lines 81, 82 with API key and Shared Secret in /src/main/java/../launch/Main.java
- edit user logins in /src/main/java/../files/Store.java with new group username logins for relative permission
- edit user logins and corresponding passwords in /src/main/java/../launch/Main.java lines 117-120
- edit album categories in /src/main/webapp/upload.jsp line 38, /src/main/webapp/viewlist.jsp line 38
- pair RSS feed in /src/main/webapp/rss.jsp line 40 with your Flickr userId, [see this for specific example](http://i.imgur.com/U0iUZ1t.png)


## Initial Run and Finish Setup

TODO: update this to be a runtime flag

- uncomment lines in /src/main/java/../launch/Main.java to run authSetup(), comment out Tomcat server setup. 
- run locally and follow command line instructions for finalizing setup
- uncomment Tomcat setup, hide authSetup()


## Running Locally

Make sure you have Java and Maven installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

```sh
$ git clone https://git.heroku.com/miyafoto-clone.git
$ cd miyafoto-clone
$ mvn install
$ foreman start web
```

With default settings, miyafoto should be running on [localhost:5000](http://localhost:5000/).

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

